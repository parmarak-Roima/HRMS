package com.HRMS.HRMS.Controllers.AchivementController;

import com.HRMS.HRMS.dto.AchievementDtos.*;
import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.service.AchievementServices.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    // ─────────────────────────────────────────────
    // FEED
    // ─────────────────────────────────────────────
    @GetMapping("/feed")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<ApiResponse<List<AchievementPostResponseDto>>> getFeed(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = to != null ? to.atTime(23, 59, 59) : null;
        List<AchievementPostResponseDto> response = achievementService.getFeed(user.getId(), authorId, tagName, fromDateTime, toDateTime);
        return ResponseEntity.ok(new ApiResponse<>("Feed fetched successfully", response));
    }
//    Long empId,int pageNo , int pageSize , String sortDir,String sortBy
//    @GetMapping("/feed")
//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
//    public ResponseEntity<ApiResponse<List<AchievementPostResponseDto>>> getFeed(
//            @RequestParam(required = false) int pageNo,
//            @RequestParam(required = false) int pageSize,
//            @RequestParam(required = false) String sortBy,
//            @RequestParam(required = false) String sortDir
//    ) {
//        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        List<AchievementPostResponseDto> response = achievementService.getFeedPageable(user.getId(), pageNo, pageSize, sortDir, sortBy);
//        return ResponseEntity.ok(new ApiResponse<>("Feed fetched successfully", response));
//    }


    // ─────────────────────────────────────────────
    // POSTS
    // ─────────────────────────────────────────────

    // consumes multipart/form-data for file uploads
    @PostMapping(value = "/posts", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<ApiResponse<AchievementPostResponseDto>> createPost(
            @ModelAttribute CreatePostRequestDto request
    ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AchievementPostResponseDto response = achievementService.createPost(user.getId(), request);
        return ResponseEntity.ok(new ApiResponse<>("Post created successfully", response));
    }

    // Edit stays as JSON — no file change on edit
    @PutMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<ApiResponse<AchievementPostResponseDto>> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostRequestDto request
    ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AchievementPostResponseDto response = achievementService.updatePost(postId, user.getId(), request);
        return ResponseEntity.ok(new ApiResponse<>("Post updated successfully", response));
    }

    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @RequestParam(required = false) String reason
    ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        achievementService.deletePost(postId, user.getId(), user.getRole(), reason);
        return ResponseEntity.ok(new ApiResponse<>("Post deleted successfully", null));
    }

    // ─────────────────────────────────────────────
    // LIKES
    // ─────────────────────────────────────────────

    @PostMapping("/posts/{postId}/like")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<ApiResponse<LikeResponseDto>> toggleLike(
            @PathVariable Long postId
    ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LikeResponseDto response = achievementService.toggleLike(postId, user.getId());
        return ResponseEntity.ok(new ApiResponse<>("Like toggled successfully", response));
    }

    // ─────────────────────────────────────────────
    // COMMENTS
    // ─────────────────────────────────────────────

    @GetMapping("/posts/{postId}/comments")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getComments(
            @PathVariable Long postId
    ) {
        List<CommentResponseDto> response = achievementService.getComments(postId);
        return ResponseEntity.ok(new ApiResponse<>("Comments fetched successfully", response));
    }

    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<ApiResponse<CommentResponseDto>> addComment(
            @PathVariable Long postId,
            @RequestBody AddCommentRequestDto request
    ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommentResponseDto response = achievementService.addComment(postId, user.getId(), request);
        return ResponseEntity.ok(new ApiResponse<>("Comment added successfully", response));
    }

    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequestDto request
    ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommentResponseDto response = achievementService.updateComment(commentId, user.getId(), request);
        return ResponseEntity.ok(new ApiResponse<>("Comment updated successfully", response));
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @RequestParam(required = false) String reason
    ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        achievementService.deleteComment(commentId, user.getId(), user.getRole(), reason);
        return ResponseEntity.ok(new ApiResponse<>("Comment deleted successfully", null));
    }
}