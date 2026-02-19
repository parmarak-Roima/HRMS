package com.HRMS.HRMS.dto.GameDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameInterestedResponseDto {
    private Long gameId;
    private String gameName;
    private boolean isInterested;
    private int playedInCurrentCycle;
}
