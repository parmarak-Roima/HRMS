package com.HRMS.HRMS.dto.AchievementDtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentRequestDto {
    private String text;
    private Long parentCommentId; // null for top-level comment
}
