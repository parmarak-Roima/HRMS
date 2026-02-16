package com.HRMS.HRMS.dto.JobDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class JobOpeningCreateDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Summary is required")
    private String summary;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "HR Owner ID is required")
    private Long hrOwnerId;

    @NotNull(message = "Job Description should not be null")
    MultipartFile jdFile;

    private List<Long> cvReviewerIds;
}
