package com.HRMS.HRMS.dto.GameDtos;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResponseDto {
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int slotDuration; //(in minutes)
    private int minPlayers;
    private int maxPlayers;
    private int currentCycle;
}
