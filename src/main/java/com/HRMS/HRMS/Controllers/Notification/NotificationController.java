package com.HRMS.HRMS.Controllers.Notification;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.Notification.NotificationDto;
import com.HRMS.HRMS.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(
            NotificationService notificationService
            ){
        this.notificationService = notificationService;
    }

    @GetMapping("/{empId}")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getAllNotifications(@PathVariable Long empId) {
        List<NotificationDto> notifications = notificationService.findByUserIdOrderByCreatedAtDesc(empId);
        return ResponseEntity.ok(new ApiResponse<>("Fetched notifications", notifications));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(new ApiResponse<>("Marked as read",null));
    }

    @GetMapping("/unread-count/{empId}")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Long empId) {
        Long count = notificationService.getUnreadCount(empId);
        return ResponseEntity.ok(new ApiResponse<>("Unread count fetched", count));
    }
}
