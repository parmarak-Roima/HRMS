package com.HRMS.HRMS.dto.AchievementDtos;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long id;
    private Long postId;
    private Long authorId;
    private String authorName;
    private String authorProfileUrl;
    private String text;
    private Long parentCommentId;
    private List<CommentResponseDto> replies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
