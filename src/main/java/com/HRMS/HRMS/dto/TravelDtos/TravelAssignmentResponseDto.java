package com.HRMS.HRMS.dto.TravelDtos;

import com.HRMS.HRMS.entity.Enums.TravelStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TravelAssignmentResponseDto {
    private Long id;
    private Long travelId;
    private String destination;
    private Long employeeId;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private TravelStatus status;
}

