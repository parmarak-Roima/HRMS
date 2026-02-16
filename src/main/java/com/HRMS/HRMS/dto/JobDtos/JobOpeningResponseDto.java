package com.HRMS.HRMS.dto.JobDtos;
import com.HRMS.HRMS.entity.JobEntities.JobOpening.JobStatus;
import lombok.Data;

@Data
public class JobOpeningResponseDto {
    private Long id;
    private String title;
    private String summary;
    private String description;
    private String jdFileUrl;
    private JobStatus status;
    private Long hrOwnerId;
    private String hrOwnerName;
}
