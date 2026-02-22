package com.HRMS.HRMS.repository.AchievementRepositories;

import com.HRMS.HRMS.entity.Achivements.AchievementPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementPostRepository extends JpaRepository<AchievementPost, Long> {

    // Feed: all non-deleted posts, newest first
    List<AchievementPost> findByIsDeletedFalseOrderByCreatedAtDesc();

    // Filter by author
    List<AchievementPost> findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(Long authorId);

    // Filter by date range
    List<AchievementPost> findByIsDeletedFalseAndCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime from, LocalDateTime to);

    // Filter by author + date range
    List<AchievementPost> findByAuthorIdAndIsDeletedFalseAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long authorId, LocalDateTime from, LocalDateTime to);

    // Single non-deleted post fetch
    Optional<AchievementPost> findByIdAndIsDeletedFalse(Long id);


    // Filter by tag name → returns IDs only, then fetch posts by IDs
    @Query("""
            SELECT DISTINCT pt.post.id FROM PostTag pt
            WHERE pt.tag.name = :tagName
            AND pt.post.isDeleted = false
            """)
    List<Long> findPostIdsByTagName(@Param("tagName") String tagName);

    // Filter by author + tag → returns IDs only
    @Query("""
            SELECT DISTINCT pt.post.id FROM PostTag pt
            WHERE pt.tag.name = :tagName
            AND pt.post.author.id = :authorId
            AND pt.post.isDeleted = false
            """)
    List<Long> findPostIdsByAuthorIdAndTagName(
            @Param("authorId") Long authorId,
            @Param("tagName") String tagName);

    // Fetch posts by IDs ordered by createdAt desc
    List<AchievementPost> findByIdInAndIsDeletedFalseOrderByCreatedAtDesc(List<Long> ids);
}