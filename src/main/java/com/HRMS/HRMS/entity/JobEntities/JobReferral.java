package com.HRMS.HRMS.entity.JobEntities;

import com.HRMS.HRMS.entity.BaseEntity;
import com.HRMS.HRMS.entity.Employee;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "job_referral")
@Data
public class JobReferral extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private JobOpening job;

    @ManyToOne
    @JoinColumn(name = "referrer_id", nullable = false)
    private Employee referrer;

    @Column(name = "candidate_name", nullable = false)
    private String candidateName;

    @Column(name = "candidate_email")
    private String candidateEmail;

    @Column(name = "resume_url", nullable = false)
    private String resumeUrl;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    private ReferralStatus status = ReferralStatus.NEW;

    public enum ReferralStatus {
        NEW, IN_REVIEW, INTERVIEWING, REJECTED, HIRED
    }
}
