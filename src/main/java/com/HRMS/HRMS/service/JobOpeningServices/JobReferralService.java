package com.HRMS.HRMS.service.JobOpeningServices;

import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.EmailDtos.ReferralEmailDto;
import com.HRMS.HRMS.dto.EmailDtos.EmailSendingDto;
import com.HRMS.HRMS.dto.JobDtos.JobReferralCreateDto;
import com.HRMS.HRMS.dto.JobDtos.JobReferralResponseDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.JobEntities.JobOpening;
import com.HRMS.HRMS.entity.JobEntities.JobReferral;
import com.HRMS.HRMS.exception.ForBiddenException;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.Config.ConfigRepository;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.JobOpeningRepositories.JobOpeningRepository;
import com.HRMS.HRMS.repository.JobOpeningRepositories.JobReferralRepository;
import com.HRMS.HRMS.service.Config.ConfigService;
import com.HRMS.HRMS.service.DocumentService;
import com.HRMS.HRMS.service.Email.EmailContentBuilder;
import com.HRMS.HRMS.service.Email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
public class JobReferralService {

     private final JobReferralRepository referralRepo;
     private final JobOpeningRepository jobRepo;
     private final EmployeeRepository employeeRepo;
     private final DocumentService documentService; // Cloudinary Service
     private final EmailService emailService;
     private final ModelMapper modelMapper;
     private final EmailContentBuilder emailContentBuilder;
     private final ConfigService configService;
    @Autowired
    public JobReferralService(
            JobReferralRepository jobReferralRepository,
            JobOpeningRepository jobOpeningRepository,
            EmployeeRepository employeeRepository,
            DocumentService documentService,
            EmailService emailService,
            ModelMapper modelMapper,
            EmailContentBuilder emailContentBuilder,
            ConfigService configService
    ){
        this.referralRepo = jobReferralRepository;
        this.documentService= documentService;
        this.employeeRepo = employeeRepository;
        this.jobRepo = jobOpeningRepository;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.emailContentBuilder =emailContentBuilder;
        this.configService = configService;
    }

    public JobReferral createReferral(JobReferralCreateDto dto) {
        //fetch job and referrer
        JobOpening job = jobRepo.findById(dto.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job Opening not found"));
        Employee referrer = employeeRepo.findById(dto.getReferrerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        //upload
        String resumeUrl = documentService.uploadFile(dto.getCvFile(), "referrals", dto.getReferrerId(), false);
        JobReferral referral = new JobReferral();
        referral.setCandidateName(dto.getCandidateName());
        referral.setCandidateEmail(dto.getCandidateEmail());
        referral.setNote(dto.getNote());
        referral.setJob(job);
        referral.setReferrer(referrer);
        referral.setResumeUrl(resumeUrl);

//        sendMail(referral);
        log.info("referral created for job opening ( "+ job.getId() + ") for role"+ job.getTitle() +"for email id"+referral.getCandidateEmail()+"by"+referral.getCandidateName());
        return referralRepo.save(referral);
    }

    public List<JobReferralResponseDto> getAllJobReferralByJobOpening(Long jobOpeningId) {
        List<JobReferral> jobReferrals = referralRepo.findJobReferralByJobId(jobOpeningId);
        return jobReferrals.stream().map(
                jobReferral -> {
                    JobReferralResponseDto dto =  new JobReferralResponseDto();
                    dto.setId(jobReferral.getId());
                    dto.setNote(jobReferral.getNote());
                    dto.setJobTitle(jobReferral.getJob().getTitle());
                    dto.setCandidateEmail(jobReferral.getCandidateEmail());
                    dto.setResumeUrl(jobReferral.getResumeUrl());
                    dto.setReferrerEmail(jobReferral.getReferrer().getEmail());
                    dto.setCandidateName(jobReferral.getCandidateName());
                    dto.setStatus(jobReferral.getStatus().toString());
                    return dto;
                }
        ).toList();
    }

    private void sendMail(JobReferral referral){
        //create body for mail
        String body = emailContentBuilder.buildEmail("referral",new ReferralEmailDto(
               referral.getJob().getTitle() , referral.getCandidateName(),referral.getCandidateEmail(),
                referral.getReferrer().getName(),referral.getNote()
        ));
        //send mail to cv reviewers
        referral.getJob().getCvReviewers().forEach(
                cr -> emailService.sendEmailWithAttachment(
                        new EmailSendingDto(
                                body , cr.getEmail() ,"Referral received !!",referral.getResumeUrl()
                        )
                )
        );
        //send to specific hr mail which is configured on database
        emailService.sendEmailWithAttachment(
                new EmailSendingDto(
                        body,configService.findValueByKey("HR_DEFAULT"),"Referral received !!",referral.getResumeUrl()
                )
        );
        //send mail to hr owner
        emailService.sendEmailWithAttachment(
                new EmailSendingDto(
                        body , referral.getJob().getHrOwner().getEmail() ,"Referral received !!",referral.getResumeUrl()
                )
        );
    }

    public String updateJobReferralStatus(Long jobReferralId, String status, CustomUserPrincipal user) {
         JobReferral jobReferral = referralRepo.findById(jobReferralId).orElseThrow( () ->
                new ResourceNotFoundException("job referral doesn't exits!!")
        );
         String oldStatus = jobReferral.getStatus().toString();
         //check if user assigned to this job opening or not
         if(user.getRole().equals("EMPLOYEE") || user.getRole().equals("MANAGER") ) {
             List<Long> cvReviewersId = jobReferral.getJob().getCvReviewers().stream().map(
                     Employee::getId
             ).toList();

             if (!cvReviewersId.contains(user.getId())) {
                 throw new ForBiddenException("You have not access to update status of this referral!!");
             }
         }
        try {
            JobReferral.ReferralStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("status should be NEW, IN_REVIEW, INTERVIEWING, REJECTED, HIRED");
        }
        jobReferral.setStatus(JobReferral.ReferralStatus.valueOf(status.toUpperCase()));
        referralRepo.save(jobReferral);
        log.info("job referral (id:- "+ jobReferral.getId() + ")status changed from " + oldStatus + "to"+jobReferral.getStatus().toString());
        return jobReferral.getStatus().toString();
    }
}
