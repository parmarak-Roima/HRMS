package com.HRMS.HRMS.dto.AchievementDtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDto {
    private String url;
    private String type; // IMAGE, VIDEO, DOCUMENT, OTHER
}
