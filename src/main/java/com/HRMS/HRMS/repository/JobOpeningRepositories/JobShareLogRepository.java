package com.HRMS.HRMS.repository.JobOpeningRepositories;

import com.HRMS.HRMS.entity.JobEntities.JobShareLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobShareLogRepository extends JpaRepository<JobShareLog, Long> {
}
