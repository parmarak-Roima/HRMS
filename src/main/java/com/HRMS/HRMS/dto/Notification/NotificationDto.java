package com.HRMS.HRMS.dto.Notification;

import lombok.Data;
import java.time.LocalDateTime;


@Data
public class NotificationDto {
    private Long id;
    private String message;
    private String type;
    private Long referenceId;
    private boolean isRead = false;
    private LocalDateTime createdAt = LocalDateTime.now();
}
