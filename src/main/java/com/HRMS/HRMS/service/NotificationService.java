package com.HRMS.HRMS.service;

import com.HRMS.HRMS.dto.NotificationDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Notification;
import com.HRMS.HRMS.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, ModelMapper modelMapper){
        this.notificationRepository = notificationRepository;
        this.modelMapper = modelMapper;
    }

    public List<NotificationDto> findByUserIdOrderByCreatedAtDesc(Long empId){
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(empId).stream().map(
                notification -> {
                    return modelMapper.map(notification,NotificationDto.class);
                }
        ).toList();
    }

    public void sendNotification(Employee recipient, String message, String type, Long referenceId) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setRead(false);
        log.info("notification sent to"+ notification.getUser().getEmail() + "of type"+
                notification.getType()+ "by" + notification.getReferenceId() +"for"+ notification.getMessage() );
        notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId).orElseThrow();
        n.setRead(true);
        notificationRepository.save(n);
    }

    public long getUnreadCount(Long empId) {
        return notificationRepository.countByUserIdAndIsReadFalse(empId);
    }
}
