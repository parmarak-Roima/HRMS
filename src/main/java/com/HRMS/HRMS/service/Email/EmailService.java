package com.HRMS.HRMS.service.Email;
import com.HRMS.HRMS.dto.EmailDtos.EmailSendingDto;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {


    private final JavaMailSender mailSender;

    @Autowired
    public EmailService( JavaMailSender javaMailSender ){
        this.mailSender = javaMailSender;
    }

    public void sendEmailWithAttachment(EmailSendingDto dto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(dto.getTo());
            helper.setSubject(dto.getSubject());
            helper.setText(dto.getBody(), true);

            if (dto.getAttachmentUrl() != null && !dto.getAttachmentUrl().isEmpty()) {
                UrlResource resource = new UrlResource(dto.getAttachmentUrl());
                if (!resource.exists() || !resource.isReadable()) {
                    throw new IOException("Attachment not found or not readable: " + dto.getAttachmentUrl());
                }
                String fileName = resource.getFilename();
                if (fileName == null) fileName = "Attachment.pdf";
                helper.addAttachment(fileName, resource);
            }
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}

