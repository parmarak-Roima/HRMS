package com.HRMS.HRMS.repository.AchievementRepositories;

import com.HRMS.HRMS.entity.Achivements.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostIdAndEmployeeId(Long postId, Long employeeId);

    boolean existsByPostIdAndEmployeeId(Long postId, Long employeeId);

    long countByPostId(Long postId);
}
