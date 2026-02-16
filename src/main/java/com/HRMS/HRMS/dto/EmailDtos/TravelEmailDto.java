package com.HRMS.HRMS.dto.EmailDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TravelEmailDto {
    private String name;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;


}
