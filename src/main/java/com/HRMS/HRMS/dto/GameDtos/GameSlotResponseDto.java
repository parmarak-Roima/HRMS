package com.HRMS.HRMS.dto.GameDtos;

import com.HRMS.HRMS.entity.GameEntities.Game;
import com.HRMS.HRMS.entity.GameEntities.GameSlot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameSlotResponseDto {
    private Long id;
    private String gameName;
    private Long gameId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
}
