package com.HRMS.HRMS.dto.AchievementDtos;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePostRequestDto {
    private String title;
    private String description;
    private List<String> tags;
}
