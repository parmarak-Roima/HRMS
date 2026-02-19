package com.HRMS.HRMS.dto.GameDtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class GameCreationDto {
    @NotNull(message = "name should not be empty")
    private String name;
    @NotNull(message = "start time should not be empty")
    private LocalTime startTime;
    @NotNull(message = "end time should not be empty")
    private LocalTime endTime;
    @NotNull(message = "slot duration should not be empty")
    private int slotDuration;
    @NotNull(message = "minimum player should not be empty")
    private int minPlayers;
    @NotNull(message = "maximum player should not be empty")
    private int maxPlayers;
    private int currentCycle = 1;
}
