package com.HRMS.HRMS.dto.AchievementDtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDto {
    private Boolean liked;   // true = liked, false = unliked
    private Long likeCount;
}
