package com.HRMS.HRMS.dto.JobDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShareJobDto {

    @NotNull(message = "Job ID is required")
    private Long jobId;

    @NotNull(message = "Sender (Employee) ID is required")
    private Long sharedById;

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Please provide a valid email address")
    private String recipientEmail;
}
