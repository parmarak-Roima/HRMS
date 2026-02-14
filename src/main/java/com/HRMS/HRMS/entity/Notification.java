package com.HRMS.HRMS.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Employee user;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String type;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "is_read")
    private boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
