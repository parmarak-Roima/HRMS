package com.HRMS.HRMS.Controllers.JobOpening;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.JobDtos.JobReferralCreateDto;
import com.HRMS.HRMS.dto.JobDtos.JobReferralResponseDto;
import com.HRMS.HRMS.entity.JobEntities.JobReferral;
import com.HRMS.HRMS.service.JobOpeningServices.JobReferralService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/referrals")
public class JobReferralController {

    private final JobReferralService jobReferralService;

    @Autowired
    public JobReferralController(
            JobReferralService jobReferralService
    ){
        this.jobReferralService = jobReferralService;
    }

    //create referral
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Long>> submitReferral(
             @ModelAttribute @Valid JobReferralCreateDto dto) {

        JobReferral savedReferral = jobReferralService.createReferral(dto);
        return new ResponseEntity<>(
                new ApiResponse<>("Referral submitted successfully!", savedReferral.getId()),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{JobOpeningId}")
    public ResponseEntity<ApiResponse<List<JobReferralResponseDto>>> allReferralForJobOpening(
            @PathVariable Long JobOpeningId
    ){
        List<JobReferralResponseDto> responseDtos = jobReferralService.getAllJobReferralByJobOpening(JobOpeningId);
        return new ResponseEntity<>(
                new ApiResponse<>("All referrals fetched successfully !!",responseDtos),
                HttpStatus.OK
        );
    }

    @GetMapping("/Employee/{JobOpeningId}")
    public ResponseEntity<ApiResponse<List<JobReferralResponseDto>>> allReferralForJobOpeningAndEmp(
            @PathVariable Long JobOpeningId
    ){
        CustomUserPrincipal user = (CustomUserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<JobReferralResponseDto> responseDtos = jobReferralService.getAllJobReferralForJobAndReferrer(JobOpeningId,user.getId());
        return new ResponseEntity<>(
                new ApiResponse<>("All referrals fetched successfully !!",responseDtos),
                HttpStatus.OK
        );
    }

    @PatchMapping("{jobReferralID}/{status}")
    public ResponseEntity<ApiResponse<String>> updateJobReferral(@PathVariable Long jobReferralID , @PathVariable String status ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(
                new ApiResponse<>("Status updated successFully for referral!", jobReferralService.updateJobReferralStatus(jobReferralID,status,user)),
                HttpStatus.OK
        );
    }

    @GetMapping("/Employee2/{JobOpeningId}/{email}")
    public ResponseEntity<ApiResponse<Boolean>> exits(
            @PathVariable Long JobOpeningId , @PathVariable String email
    ){

        return new ResponseEntity<>(
                new ApiResponse<>("!!",jobReferralService.exitsByJobIdReferrarIdAnd(JobOpeningId,email)),
                HttpStatus.OK
        );
    }

}
