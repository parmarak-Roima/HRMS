package com.HRMS.HRMS.Controllers.JobOpening;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.JobDtos.JobOpeningResponseDto;
import com.HRMS.HRMS.dto.JobDtos.JobOpeningCreateDto;
import com.HRMS.HRMS.dto.JobDtos.ShareJobDto;
import com.HRMS.HRMS.dto.PaginatedResponseDto;
import com.HRMS.HRMS.service.JobOpeningServices.JobOpeningService;
import com.HRMS.HRMS.service.JobOpeningServices.JobShareService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobOpeningController {

    private final JobOpeningService jobOpeningService;

    private final JobShareService jobShareService;

    public JobOpeningController(
            JobOpeningService jobOpeningService,
            JobShareService jobShareService
    ) {
        this.jobOpeningService = jobOpeningService;
        this.jobShareService = jobShareService;
    }

    //creation
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ApiResponse<JobOpeningResponseDto>> createJobOpening(
            @ModelAttribute @Valid JobOpeningCreateDto dto) {
        JobOpeningResponseDto jobOpeningResponseDto = jobOpeningService.createJobOpening(dto);
        return new ResponseEntity<>(
                new ApiResponse<>("Job opening created successfully!!!", jobOpeningResponseDto),
                HttpStatus.CREATED
        );
    }

    //all jobs by status
//    @GetMapping("/active")
//    public ResponseEntity<ApiResponse<List<JobOpeningResponseDto>>> getActiveJobs() {
//        List<JobOpeningResponseDto> activeJobs = jobOpeningService.getAllJobs();
//        return ResponseEntity.ok(new ApiResponse<>("Fetched all active job openings", activeJobs));
//    }

    // job by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobOpeningResponseDto>> getJobById(@PathVariable Long id) {
        JobOpeningResponseDto job = jobOpeningService.getJobById(id);
        return ResponseEntity.ok(new ApiResponse<>("Fetched job details", job));
    }

    //update job status
    @PatchMapping("{id}/{status}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ApiResponse<String>> updateJob(@PathVariable Long id , @PathVariable String status ) {
        return new ResponseEntity<>(
                new ApiResponse<>("Status updated successFully!", jobOpeningService.updateJobOpeningStatus(id,status)),
                HttpStatus.OK
        );
    }

    //job sharing
    @PostMapping("/share")
    public ResponseEntity<ApiResponse<String>> shareJob(@Valid @RequestBody ShareJobDto dto) {
        jobShareService.shareJob(dto);
        return ResponseEntity.ok(new ApiResponse<>("Job successfully shared with" + dto.getRecipientEmail() , dto.getRecipientEmail()));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<JobOpeningResponseDto>> getAllJobs(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "3", required = false) int pageSize
    ) {
        return ResponseEntity.ok(jobOpeningService.getAllJobsByPage(pageNo, pageSize));
    }


}
