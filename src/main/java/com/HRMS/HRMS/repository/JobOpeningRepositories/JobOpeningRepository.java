package com.HRMS.HRMS.repository.JobOpeningRepositories;

import com.HRMS.HRMS.entity.JobEntities.JobOpening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobOpeningRepository extends JpaRepository<JobOpening, Long> {
    List<JobOpening> findByStatus(JobOpening.JobStatus status);
}
