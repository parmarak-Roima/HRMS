package com.HRMS.HRMS.dto.TravelDtos;
import com.HRMS.HRMS.entity.Enums.TravelStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TravelAssignmentUpdateDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private TravelStatus status;
}
