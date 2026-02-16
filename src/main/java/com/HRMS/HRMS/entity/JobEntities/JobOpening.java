package com.HRMS.HRMS.entity.JobEntities;
import com.HRMS.HRMS.entity.BaseEntity;
import com.HRMS.HRMS.entity.Employee;
import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "job_opening")
@Data
public class JobOpening extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "jd_file_url")
    private String jdFileUrl;

    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "hr_owner_id")
    private Employee hrOwner;

    @ManyToMany
    @JoinTable(
            name = "job_cv_reviewers",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> cvReviewers = new HashSet<>();

    public enum JobStatus {
        ACTIVE, CLOSED
    }
}
