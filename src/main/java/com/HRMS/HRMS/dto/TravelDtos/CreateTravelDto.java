package com.HRMS.HRMS.dto.TravelDtos;
import com.HRMS.HRMS.entity.Enums.TravelStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateTravelDto {

    @NotBlank(message = "Destination is required")
    private String destination;

    private String description;

    @NotNull(message = "Start Date is required")
    @FutureOrPresent(message = "Start Date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End Date is required")
    @FutureOrPresent(message = "End Date must be today or in the future")
    private LocalDate endDate;

    private TravelStatus status = TravelStatus.SCHEDULED; // Optional for Create (Defaults to SCHEDULED)

    private String requiredDocs;
    //List of Employee IDs to assign immediately
    private List<Long> employeeIdsToAssign;
}

