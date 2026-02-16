package com.HRMS.HRMS.service.JobOpeningServices;

import com.HRMS.HRMS.dto.JobDtos.JobOpeningCreateDto;
import com.HRMS.HRMS.dto.JobDtos.JobOpeningResponseDto;
import com.HRMS.HRMS.dto.TravelDtos.ShowTravelDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.JobEntities.JobOpening;
import com.HRMS.HRMS.exception.BadRequestException;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.JobOpeningRepositories.JobOpeningRepository;
import com.HRMS.HRMS.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Transactional
@Slf4j
public class JobOpeningService {

     private final JobOpeningRepository jobRepo;
     private final EmployeeRepository employeeRepo;
     private final DocumentService documentService; // Your Cloudinary service
     private final ModelMapper modelMapper;

    @Autowired
    public JobOpeningService(
            JobOpeningRepository jobOpeningRepository,
            EmployeeRepository employeeRepository,
            DocumentService documentService,
            ModelMapper modelMapper
    ){
        this.jobRepo = jobOpeningRepository;
        this.employeeRepo = employeeRepository;
        this.documentService = documentService;
        this.modelMapper = modelMapper;
    }

    public JobOpeningResponseDto createJobOpening(JobOpeningCreateDto dto) {
        JobOpening job =  modelMapper.map(dto,JobOpening.class);
        job.setId(null);
        Employee hrOwner = employeeRepo.findById(dto.getHrOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("HR Owner not found"));
        job.setHrOwner(hrOwner);
        if (dto.getCvReviewerIds() != null && !dto.getCvReviewerIds().isEmpty()) {
            List<Employee> reviewersList = employeeRepo.findAllById(dto.getCvReviewerIds());
            Set<Employee> reviewersSet = new HashSet<>(reviewersList);
            job.setCvReviewers(reviewersSet);
        }
        if (dto.getJdFile() != null && !dto.getJdFile().isEmpty()) {
            String fileUrl = documentService.uploadFile(dto.getJdFile(), "job_descriptions", hrOwner.getId(), false);
            job.setJdFileUrl(fileUrl);
        }
        JobOpening savedJob = jobRepo.save(job);

        //logging
        log.info("job opening created by "+ job.getHrOwner().getName() + "for role" + job.getTitle() );

        return mapToResponse(savedJob);
    }

    public List<JobOpeningResponseDto> getAllActiveJobs() {
        return jobRepo.findByStatus(JobOpening.JobStatus.ACTIVE)
                .stream()
                .map(
                        this::mapToResponse
                )
                .toList();
    }

    public JobOpeningResponseDto getJobById(Long id) {
        JobOpening job = jobRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job Opening not found with ID: " + id));
        return mapToResponse(job);
    }

    public String updateJobOpeningStatus(Long id, String status) {
        JobOpening jobOpening = jobRepo.findById(id).orElseThrow( () ->
                new ResourceNotFoundException("job Opening doesn't exits")
        );
        String oldStatus = jobOpening.getStatus().toString();
        try {
            JobOpening.JobStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("status should be active or closed");
        }
        jobOpening.setStatus(JobOpening.JobStatus.valueOf(status.toUpperCase()));
        jobRepo.save(jobOpening);
        log.info("job opening id :-"+ jobOpening.getId() + "status changed from " + oldStatus + "to"+jobOpening.getStatus());
        return jobOpening.getStatus().toString();
    }

    private JobOpeningResponseDto mapToResponse(JobOpening job) {
        JobOpeningResponseDto jobOpeningResponseDto= modelMapper.map(job,JobOpeningResponseDto.class);
        jobOpeningResponseDto.setHrOwnerId(job.getHrOwner().getId());
        jobOpeningResponseDto.setHrOwnerName(job.getHrOwner().getName());
        return jobOpeningResponseDto;
    }
}
