package com.HRMS.HRMS.dto.AchievementDtos;

import com.HRMS.HRMS.dto.AchievementDtos.AttachmentDto;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequestDto {
    private String title;
    private String description;
    private List<String> tags;
    private List<AttachmentDto> attachments;
}
