package com.HRMS.HRMS.dto.TravelDtos;

import com.HRMS.HRMS.dto.AuthDtos.EmployeeIdEmailDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Enums.TravelStatus;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ShowTravelDto {
    private Long id;
    private Long created_by_id;
    private String destination;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private TravelStatus status = TravelStatus.SCHEDULED; // Optional for Create (Defaults to SCHEDULED)
    private String requiredDocs;
    private List<EmployeeIdEmailDto> employeeIdsToAssign;
}


