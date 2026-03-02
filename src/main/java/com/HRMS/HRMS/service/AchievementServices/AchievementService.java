package com.HRMS.HRMS.service.AchievementServices;

import com.HRMS.HRMS.dto.AchievementDtos.*;
import com.HRMS.HRMS.dto.EmailDtos.EmailSendingDto;
import com.HRMS.HRMS.entity.Achivements.*;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.exception.ForBiddenException;
import com.HRMS.HRMS.repository.AchievementRepositories.*;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.service.DocumentService;
import com.HRMS.HRMS.service.Email.EmailContentBuilder;
import com.HRMS.HRMS.service.Email.EmailService;
import com.HRMS.HRMS.service.NotificationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementPostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final PostLikeRepository likeRepository;
    private final TagRepository tagRepository;
    private final WarningLogRepository warningLogRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private final EmailContentBuilder emailContentBuilder;
    private final NotificationService notificationService;
    private final DocumentService documentService;
    private final EntityManager em;

    // ─────────────────────────────────────────────
    // FEED
    // ─────────────────────────────────────────────

    public  List<AchievementPostResponseDto> getFeedPageable(
          Long empId,int pageNo , int pageSize , String sortDir,String sortBy
    ){
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<AchievementPost> achievementPosts = postRepository.findAll(pageable);
        return achievementPosts.getContent().stream()
                .map(p -> mapToPostResponse(p,empId ))
                .toList();
    }

    public List<AchievementPostResponseDto> getFeedUsingCriteriaQuery(int startingPostNo, int pageSize, Long currentEmployeeId, Long authorId, String tagName, LocalDateTime from, LocalDateTime to) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AchievementPost> cq = cb.createQuery(AchievementPost.class);
        Root<AchievementPost> post = cq.from(AchievementPost.class);

        List<Predicate> predicates = new ArrayList<>();

        //check for deleted posts
        predicates.add(cb.isFalse(post.get("isDeleted")));

       //author filter
        if (authorId != null) {
            Join<Object, Object> postAuthors = post.join("author");
            predicates.add(cb.equal(postAuthors.get("id"), authorId));
        }

        //tag-name filter
        if (tagName != null && !tagName.isBlank()) {
            Join<Object, Object> tags = post.join("postTags").join("tag");
            predicates.add(cb.equal(tags.get("name"), tagName));
        }

        if (from != null && to != null) {
            predicates.add(cb.between(post.get("createdAt"), from, to));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        //  Ordering
        cq.orderBy(cb.desc(post.get("createdAt")));

        Query query = em.createQuery(cq);

        query.setFirstResult(startingPostNo);
        query.setMaxResults(pageSize);

        List<AchievementPost> posts = query.getResultList();

        return posts.stream()
                .map(p -> mapToPostResponse(p, currentEmployeeId))
                .collect(Collectors.toList());
    }

    public List<AchievementPostResponseDto> getFeed(Long currentEmployeeId,
                                                    Long authorId,
                                                    String tagName,
                                                    LocalDateTime from,
                                                    LocalDateTime to) {
        List<AchievementPost> posts;

        boolean hasAuthor = authorId != null;
        boolean hasTag = tagName != null && !tagName.isBlank();
        boolean hasDate = from != null && to != null;

        if (hasAuthor && hasTag) {
            List<Long> ids = postRepository.findPostIdsByAuthorIdAndTagName(authorId, tagName);
            posts = ids.isEmpty() ? List.of() : postRepository.findByIdInAndIsDeletedFalseOrderByCreatedAtDesc(ids);
        } else if (hasAuthor && hasDate) {
            posts = postRepository.findByAuthorIdAndIsDeletedFalseAndCreatedAtBetweenOrderByCreatedAtDesc(authorId, from, to);
        } else if (hasAuthor) {
            posts = postRepository.findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(authorId);
        } else if (hasTag) {
            List<Long> ids = postRepository.findPostIdsByTagName(tagName);
            posts = ids.isEmpty() ? List.of() : postRepository.findByIdInAndIsDeletedFalseOrderByCreatedAtDesc(ids);
        } else if (hasDate) {
            posts = postRepository.findByIsDeletedFalseAndCreatedAtBetweenOrderByCreatedAtDesc(from, to);
        } else {
            posts = postRepository.findByIsDeletedFalseOrderByCreatedAtDesc();
        }

        return posts.stream()
                .map(p -> mapToPostResponse(p, currentEmployeeId))
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // POST CRUD
    // ─────────────────────────────────────────────

    @Transactional
    public AchievementPostResponseDto createPost(Long authorId, CreatePostRequestDto request) {
        Employee author = employeeRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        AchievementPost post = AchievementPost.builder()
                .author(author)
                .title(request.getTitle())
                .description(request.getDescription())
                .isSystemGenerated(false)
                .build();

        // Handle tags — create if not exists
        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByName(tagName.toLowerCase().trim())
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName.toLowerCase().trim()).build()));
                PostTag postTag = PostTag.builder().post(post).tag(tag).build();
                post.getPostTags().add(postTag);
            }
        }

        // Handle file uploads via Cloudinary documentService
        if (request.getFiles() != null) {
            for (MultipartFile file : request.getFiles()) {
                if (file != null && !file.isEmpty()) {
                    String uploadedUrl = documentService.uploadFile(file, "achievements", authorId, false);
                    // Detect type from content type
                    String contentType = file.getContentType() != null ? file.getContentType().toLowerCase() : "";
                    PostAttachment.AttachmentType type;
                    if (contentType.startsWith("image")) {
                        type = PostAttachment.AttachmentType.IMAGE;
                    } else if (contentType.startsWith("video")) {
                        type = PostAttachment.AttachmentType.VIDEO;
                    } else if (contentType.contains("pdf") || contentType.contains("document")) {
                        type = PostAttachment.AttachmentType.DOCUMENT;
                    } else {
                        type = PostAttachment.AttachmentType.OTHER;
                    }
                    PostAttachment attachment = PostAttachment.builder()
                            .post(post)
                            .attachmentUrl(uploadedUrl)
                            .attachmentType(type)
                            .build();
                    post.getAttachments().add(attachment);
                }
            }
        }

        AchievementPost saved = postRepository.save(post);
        return mapToPostResponse(saved, authorId);
    }

    @Transactional
    public AchievementPostResponseDto updatePost(Long postId, Long requesterId, UpdatePostRequestDto request) {
        AchievementPost post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(requesterId)) {
            throw new ForBiddenException("You are not authorized to edit this post");
        }

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());

        // Reset and re-add tags
        post.getPostTags().clear();
        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByName(tagName.toLowerCase().trim())
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName.toLowerCase().trim()).build()));
                post.getPostTags().add(PostTag.builder().post(post).tag(tag).build());
            }
        }

        return mapToPostResponse(postRepository.save(post), requesterId);
    }

    @Transactional
    public void deletePost(Long postId, Long requesterId, String requesterRole, String reason) {
        AchievementPost post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isOwner = post.getAuthor() != null && post.getAuthor().getId().equals(requesterId);
        boolean isHR = "HR".equalsIgnoreCase(requesterRole);

        if (!isOwner && !isHR) {
            throw new ForBiddenException("You are not authorized to delete this post");
        }

        post.setIsDeleted(true);
        postRepository.save(post);

        // HR deleting someone else's post → warning
        if (isHR && !isOwner && post.getAuthor() != null) {
            handleHrWarning(requesterId, post.getAuthor(), WarningLog.ContentType.POST, postId, reason);
        }
    }

    // ─────────────────────────────────────────────
    // LIKES
    // ─────────────────────────────────────────────

    @Transactional
    public LikeResponseDto toggleLike(Long postId, Long employeeId) {
        AchievementPost post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        boolean alreadyLiked = likeRepository.existsByPostIdAndEmployeeId(postId, employeeId);

        if (alreadyLiked) {
            PostLike like = likeRepository.findByPostIdAndEmployeeId(postId, employeeId).get();
            likeRepository.delete(like);
        } else {
            PostLike like = PostLike.builder().post(post).employee(employee).build();
            likeRepository.save(like);

            // Notify post author (skip if system post or self-like)
            if (post.getAuthor() != null && !post.getAuthor().getId().equals(employeeId)) {
                notificationService.sendNotification(
                        post.getAuthor(),
                        employee.getName() + " liked your post!",
                        "achievement",
                        employeeId
                );
            }
        }

        long likeCount = likeRepository.countByPostId(postId);
        return new LikeResponseDto(!alreadyLiked, likeCount);
    }

    // ─────────────────────────────────────────────
    // COMMENTS
    // ─────────────────────────────────────────────

    @Transactional
    public CommentResponseDto addComment(Long postId, Long authorId, AddCommentRequestDto request) {
        AchievementPost post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Employee author = employeeRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        PostComment comment = PostComment.builder()
                .post(post)
                .author(author)
                .text(request.getText())
                .build();

        // Handle reply
        if (request.getParentCommentId() != null) {
            PostComment parent = commentRepository.findByIdAndIsDeletedFalse(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parent);
        }

        PostComment saved = commentRepository.save(comment);

        // Notify post author
        if (post.getAuthor() != null && !post.getAuthor().getId().equals(authorId)) {
            notificationService.sendNotification(
                    post.getAuthor(),
                    author.getName() + " commented on your post!",
                    "achievement",
                    authorId
            );
        }

        return mapToCommentResponse(saved);
    }

    @Transactional
    public CommentResponseDto updateComment(Long commentId, Long requesterId, UpdateCommentRequestDto request) {
        PostComment comment = commentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthor().getId().equals(requesterId)) {
            throw new ForBiddenException("You are not authorized to edit this comment");
        }

        comment.setText(request.getText());
        return mapToCommentResponse(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId, Long requesterId, String requesterRole, String reason) {
        PostComment comment = commentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        boolean isOwner = comment.getAuthor().getId().equals(requesterId);
        boolean isHR = "HR".equalsIgnoreCase(requesterRole);

        if (!isOwner && !isHR) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }

        comment.setIsDeleted(true);
        commentRepository.save(comment);

        if (isHR && !isOwner) {
            handleHrWarning(requesterId, comment.getAuthor(), WarningLog.ContentType.COMMENT, commentId, reason);
        }
    }

    public List<CommentResponseDto> getComments(Long postId) {
        return commentRepository
                .findByPostIdAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // SCHEDULER — Birthday & Anniversary Auto-Posts
    // ─────────────────────────────────────────────

    @Scheduled(cron = "0 0 8 * * *") // Every day at 8 AM
    @Transactional
    public void generateCelebrationPosts() {
        LocalDate today = LocalDate.now();
        List<Employee> allEmployees = employeeRepository.findAll();

        for (Employee emp : allEmployees) {
            // Birthday check
            if (emp.getBirthdate() != null
                    && emp.getBirthdate().getMonthValue() == today.getMonthValue()
                    && emp.getBirthdate().getDayOfMonth() == today.getDayOfMonth()) {

                AchievementPost birthdayPost = AchievementPost.builder()
                        .title("🎂 Happy Birthday, " + emp.getName() + "!")
                        .description("Today is " + emp.getName() + "'s birthday! 🎉 Wish them a wonderful day!")
                        .isSystemGenerated(true)
                        .systemEventType(AchievementPost.SystemEventType.BIRTHDAY)
                        .build();
                postRepository.save(birthdayPost);
            }

            // Work Anniversary check
            if (emp.getJoiningDate() != null
                    && emp.getJoiningDate().getMonthValue() == today.getMonthValue()
                    && emp.getJoiningDate().getDayOfMonth() == today.getDayOfMonth()
                    && !emp.getJoiningDate().isEqual(today)) {

                int years = Period.between(emp.getJoiningDate(), today).getYears();
                AchievementPost anniversaryPost = AchievementPost.builder()
                        .title("🎊 Work Anniversary — " + emp.getName())
                        .description(emp.getName() + " completes " + years + " year" + (years > 1 ? "s" : "") + " at the organization! 🙌 Thank you for your dedication!")
                        .isSystemGenerated(true)
                        .systemEventType(AchievementPost.SystemEventType.ANNIVERSARY)
                        .build();
                postRepository.save(anniversaryPost);
            }
        }
    }

    // ─────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────

    private void handleHrWarning(Long hrId, Employee targetEmployee, WarningLog.ContentType type, Long contentId, String reason) {
        Employee hr = employeeRepository.findById(hrId)
                .orElseThrow(() -> new RuntimeException("HR employee not found"));

        WarningLog log = WarningLog.builder()
                .deletedBy(hr)
                .targetEmployee(targetEmployee)
                .contentType(type)
                .contentId(contentId)
                .reason(reason)
                .build();
        warningLogRepository.save(log);

        // Send warning email
        String body = emailContentBuilder.buildEmail(
                "achievement_warning",
                new AchievementWarningEmailDto(
                        targetEmployee.getName(),
                        type.name().toLowerCase(),
                        reason != null ? reason : "Inappropriate content"
                )
        );
        emailService.sendEmailWithAttachment(
                new EmailSendingDto(
                        body,
                        targetEmployee.getEmail(),
                        "Content Removal Warning",
                        null
                )
        );

        // Also notify in-app
        notificationService.sendNotification(
                targetEmployee,
                "Your " + type.name().toLowerCase() + " was removed by HR for policy violation.",
                "warning",
                hrId
        );
    }

    private AchievementPostResponseDto mapToPostResponse(AchievementPost post, Long currentEmployeeId) {
        long likeCount = likeRepository.countByPostId(post.getId());
        boolean likedByMe = likeRepository.existsByPostIdAndEmployeeId(post.getId(), currentEmployeeId);
        long commentCount = commentRepository.countByPostIdAndIsDeletedFalse(post.getId());

        List<String> tags = post.getPostTags().stream()
                .map(pt -> pt.getTag().getName())
                .collect(Collectors.toList());

        List<String> attachmentUrls = post.getAttachments().stream()
                .map(PostAttachment::getAttachmentUrl)
                .collect(Collectors.toList());

        return AchievementPostResponseDto.builder()
                .id(post.getId())
                .authorId(post.getAuthor() != null ? post.getAuthor().getId() : null)
                .authorName(post.getAuthor() != null ? post.getAuthor().getName() : "System")
                .authorProfileUrl(post.getAuthor() != null ? post.getAuthor().getProfileUrl() : null)
                .title(post.getTitle())
                .description(post.getDescription())
                .tags(tags)
                .attachmentUrls(attachmentUrls)
                .isSystemGenerated(post.getIsSystemGenerated())
                .systemEventType(post.getSystemEventType() != null ? post.getSystemEventType().name() : null)
                .likeCount(likeCount)
                .likedByMe(likedByMe)
                .commentCount(commentCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private CommentResponseDto mapToCommentResponse(PostComment comment) {
        List<CommentResponseDto> replies = comment.getReplies().stream()
                .filter(r -> !r.getIsDeleted())
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());

        return CommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .authorProfileUrl(comment.getAuthor().getProfileUrl())
                .text(comment.getText())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .replies(replies)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}