package com.HRMS.HRMS.repository.JobOpeningRepositories;

import com.HRMS.HRMS.entity.JobEntities.JobReferral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobReferralRepository extends JpaRepository<JobReferral, Long> {
    List<JobReferral> findJobReferralByJobId(Long jobId);
}
