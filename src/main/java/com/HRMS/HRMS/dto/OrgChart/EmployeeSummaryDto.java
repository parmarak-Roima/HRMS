package com.HRMS.HRMS.dto.OrgChart;


import lombok.Data;

@Data
public class EmployeeSummaryDto {
    private Long id;
    private String name;
    private String designation;
    private String profileUrl;
    private String email;
}
