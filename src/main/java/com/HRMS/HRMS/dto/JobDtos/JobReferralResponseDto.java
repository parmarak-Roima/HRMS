package com.HRMS.HRMS.dto.JobDtos;

import lombok.Data;

@Data
public class JobReferralResponseDto {
    private String jobTitle;
    private String referrerEmail;
    private String candidateName;
    private String candidateEmail;
    private String note;
    private String resumeUrl;
}
