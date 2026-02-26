package com.HRMS.HRMS.service;

import com.HRMS.HRMS.dto.Notification.NotificationDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Notification;
import com.HRMS.HRMS.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    //map emitters by employee id
    private final Map<Long, SseEmitter> userEmitters = new ConcurrentHashMap<>();

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, ModelMapper modelMapper){
        this.notificationRepository = notificationRepository;
        this.modelMapper = modelMapper;
    }

    public SseEmitter subscribe(Long empId) {
        // Set timeout to 1 hour
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);

        userEmitters.put(empId, emitter);
        //on completion of connection remove from map
        emitter.onCompletion(() -> userEmitters.remove(empId));
        //on time out remove
        emitter.onTimeout(() -> userEmitters.remove(empId));
        //on error remove
        emitter.onError((e) -> userEmitters.remove(empId));

        return emitter;
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
        Notification savedNotification = notificationRepository.save(notification);

        SseEmitter emitter = userEmitters.get(recipient.getId());
        if (emitter != null) {
            try {
                NotificationDto dto = modelMapper.map(savedNotification, NotificationDto.class);
                // Send an event named "new-notification" containing the DTO as JSON
                emitter.send(SseEmitter.event().name("new-notification").data(dto));
            } catch (IOException e) {
                userEmitters.remove(recipient.getId());
                log.error("Failed to send SSE to user " + recipient.getId(), e);
            }
        }
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
