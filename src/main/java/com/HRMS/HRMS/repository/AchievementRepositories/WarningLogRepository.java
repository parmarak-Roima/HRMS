package com.HRMS.HRMS.repository.AchievementRepositories;

import com.HRMS.HRMS.entity.Achivements.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarningLogRepository extends JpaRepository<WarningLog, Long> {

    List<WarningLog> findByTargetEmployeeIdOrderByWarnedAtDesc(Long targetEmployeeId);
}
