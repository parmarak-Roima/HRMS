package com.HRMS.HRMS.dto.AchievementDtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementWarningEmailDto {
    private String employeeName;
    private String contentType;   // "post" or "comment"
    private String reason;
}
