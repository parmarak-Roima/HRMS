package com.HRMS.HRMS.Controllers.Notification;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.Notification.NotificationDto;
import com.HRMS.HRMS.dto.PaginatedResponseDto;
import com.HRMS.HRMS.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

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

//    @GetMapping(value = "/stream/{empId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter streamNotifications(@PathVariable Long empId) {
//        return notificationService.subscribe(empId);
//    }

    //flux way
    @GetMapping(value = "/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> streamEvents(@PathVariable Long userId) {
        return notificationService.getNotificationsForUser(userId);
    }

    @GetMapping("/{empId}")
    public ResponseEntity<ApiResponse<PaginatedResponseDto<NotificationDto>>> getAllNotifications(
            @PathVariable Long empId,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "3", required = false) int pageSize) {
        PaginatedResponseDto<NotificationDto> notifications = notificationService.findByUserIdOrderByCreatedAtDesc(pageNo,pageSize,empId);
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
