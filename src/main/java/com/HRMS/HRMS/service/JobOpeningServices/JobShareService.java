package com.HRMS.HRMS.service.JobOpeningServices;

import com.HRMS.HRMS.dto.EmailDtos.JobsharingEmailDto;
import com.HRMS.HRMS.dto.EmailDtos.EmailSendingDto;
import com.HRMS.HRMS.dto.JobDtos.ShareJobDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.JobEntities.JobOpening;
import com.HRMS.HRMS.entity.JobEntities.JobShareLog;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.JobOpeningRepositories.JobOpeningRepository;
import com.HRMS.HRMS.repository.JobOpeningRepositories.JobShareLogRepository;
import com.HRMS.HRMS.service.Email.EmailContentBuilder;
import com.HRMS.HRMS.service.Email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class JobShareService {

     private final JobShareLogRepository jobShareLogRepo;
     private final JobOpeningRepository jobRepo;
     private final EmployeeRepository employeeRepo;
    private final EmailContentBuilder emailContentBuilder;
     private final EmailService emailService;

    @Autowired
    public JobShareService(
            JobShareLogRepository jobShareLogRepository,
            JobOpeningRepository jobOpeningRepository,
            EmployeeRepository employeeRepository,
            EmailService emailService,
            EmailContentBuilder emailContentBuilder
    ){
        this.emailService = emailService;
        this.jobShareLogRepo = jobShareLogRepository;
        this.jobRepo = jobOpeningRepository;
        this.employeeRepo = employeeRepository;
        this.emailContentBuilder = emailContentBuilder;
    }

    public void shareJob(ShareJobDto dto) {
        JobOpening job = jobRepo.findById(dto.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        Employee sender = employeeRepo.findById(dto.getSharedById())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        JobShareLog shareLog = new JobShareLog();
        shareLog.setJob(job);
        shareLog.setSharedBy(sender);
        shareLog.setSharedWithEmail(dto.getRecipientEmail());
        jobShareLogRepo.save(shareLog);
        //send mail
        sendMail(shareLog);
        log.info("Job opening( id :" + shareLog.getJob().getId() +") shared by"+ shareLog.getSharedBy().getName() + "to"+shareLog.getSharedWithEmail());
    }
    private void sendMail(JobShareLog jobShareLog){
        String body = emailContentBuilder.buildEmail("sharing",new JobsharingEmailDto(
               jobShareLog.getSharedBy().getName(),
                jobShareLog.getJob().getTitle() ,
                jobShareLog.getJob().getSummary()
        ));
        emailService.sendEmailWithAttachment(
                new EmailSendingDto(
                        body , jobShareLog.getSharedWithEmail() ,"job opening",jobShareLog.getJob().getJdFileUrl()
                )
        );
    }
}
