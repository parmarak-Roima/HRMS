package com.HRMS.HRMS.entity.Achivements;

import com.HRMS.HRMS.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "warning_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // HR who deleted the content
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by", nullable = false)
    private Employee deletedBy;

    // Employee who authored the deleted content
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_employee_id", nullable = false)
    private Employee targetEmployee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    // ID of the deleted post or comment
    @Column(nullable = false)
    private Long contentId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime warnedAt = LocalDateTime.now();

    public enum ContentType {
        POST,
        COMMENT
    }
}
