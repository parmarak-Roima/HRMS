package com.HRMS.HRMS.dto.EmailDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailSendingDto {
    String body ;
    String to ;
    String subject ;
    String attachmentUrl;
}
