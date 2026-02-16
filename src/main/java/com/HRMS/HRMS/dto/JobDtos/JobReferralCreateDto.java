package com.HRMS.HRMS.dto.JobDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class JobReferralCreateDto {
    @NotNull(message = "Job ID is required")
    private Long jobId;

    @NotNull(message = "Referrer ID is required")
    private Long referrerId;

    @NotBlank(message = "Candidate name is required")
    private String candidateName;

    @Email(message = "Invalid email format")
    private String candidateEmail;

    private String note;

    @NotNull(message = "CV file is required")
    private MultipartFile cvFile;
}
