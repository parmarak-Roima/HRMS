package com.HRMS.HRMS.dto.EmailDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobsharingEmailDto {
    private String referrerName;
    private String jobTitle;
    private String jobSummary;
}
