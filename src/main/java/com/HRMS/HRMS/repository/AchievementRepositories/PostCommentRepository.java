package com.HRMS.HRMS.repository.AchievementRepositories;

import com.HRMS.HRMS.entity.Achivements.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    // Top-level comments only (no parent)
    List<PostComment> findByPostIdAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAtAsc(Long postId);

    // Replies for a specific comment
    List<PostComment> findByParentCommentIdAndIsDeletedFalseOrderByCreatedAtAsc(Long parentCommentId);

    long countByPostIdAndIsDeletedFalse(Long postId);

    Optional<PostComment> findByIdAndIsDeletedFalse(Long id);
}
