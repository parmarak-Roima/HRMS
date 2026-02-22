package com.HRMS.HRMS.dto.AchievementDtos;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementPostResponseDto {
    private Long id;
    private Long authorId;
    private String authorName;
    private String authorProfileUrl;
    private String title;
    private String description;
    private List<String> tags;
    private List<String> attachmentUrls;
    private Boolean isSystemGenerated;
    private String systemEventType;  // "BIRTHDAY" / "ANNIVERSARY" / null
    private Long likeCount;
    private Boolean likedByMe;
    private Long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
