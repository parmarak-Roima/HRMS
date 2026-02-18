package com.HRMS.HRMS.dto.GameDtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class UpdateGameDto {
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDuration; //(in minutes)
    private Integer minPlayers;
    private Integer maxPlayers;
}
