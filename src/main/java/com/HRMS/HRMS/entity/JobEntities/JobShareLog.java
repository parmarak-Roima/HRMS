package com.HRMS.HRMS.entity.JobEntities;

import com.HRMS.HRMS.entity.BaseEntity;
import com.HRMS.HRMS.entity.Employee;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_share_log")
@Data
public class JobShareLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private JobOpening job;

    @ManyToOne
    @JoinColumn(name = "shared_by_id", nullable = false)
    private Employee sharedBy;

    @Column(name = "shared_with_email", nullable = false)
    private String sharedWithEmail;
}
