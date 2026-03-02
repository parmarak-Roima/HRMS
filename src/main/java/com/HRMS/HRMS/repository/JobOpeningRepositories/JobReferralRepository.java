package com.HRMS.HRMS.repository.JobOpeningRepositories;

import com.HRMS.HRMS.entity.JobEntities.JobReferral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobReferralRepository extends JpaRepository<JobReferral, Long> {
    List<JobReferral> findJobReferralByJobId(Long jobId);
    @Query("select jr from JobReferral jr where jr.job.id = :jobId AND jr.referrer.id = :referrerId ")
    List<JobReferral> findByJobIdAndReferrarId(Long jobId , Long referrerId);

    boolean existsJobReferralByJob_IdAndCandidateEmail(Long jobId , String candidateEmail);

}
