package com.HRMS.HRMS.dto.TravelDtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class TravelAssignmentCreateDto {
    @NotNull(message = "Travel ID is required")
    private Long travelId;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;
}
