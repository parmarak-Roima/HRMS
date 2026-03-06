package com.HRMS.HRMS.service.ForgotPassWord;

import com.HRMS.HRMS.dto.AuthDtos.NewPasswordDto;
import com.HRMS.HRMS.dto.EmailDtos.EmailSendingDto;
import com.HRMS.HRMS.dto.EmailDtos.OtpEmailDto;
import com.HRMS.HRMS.dto.EmailDtos.TravelEmailDto;
import com.HRMS.HRMS.entity.EmailOtp;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmailOtpRepository;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.service.Email.EmailContentBuilder;
import com.HRMS.HRMS.service.Email.EmailService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ForgotPassService {

    private final EmailOtpRepository emailOtpRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailContentBuilder emailContentBuilder;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public ForgotPassService(EmailOtpRepository emailOtpRepository,EmployeeRepository employeeRepository,EmailContentBuilder emailContentBuilder , EmailService emailService,PasswordEncoder passwordEncoder){
        this.emailOtpRepository = emailOtpRepository;
        this.employeeRepository = employeeRepository;
        this.emailContentBuilder  = emailContentBuilder;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void sendOtp(String email){
        //find employee
        Employee employee = employeeRepository.findByEmail(email).orElseThrow(
                () ->  new ResourceNotFoundException("Employee not found with this email id"));
        emailOtpRepository.deleteByEmail(email);
        //generate otp
        double randomValue = Math.random() * 9000;
        Long otp = (long) (randomValue) + 1000;

        //store email-otp
        emailOtpRepository.save(
               EmailOtp.builder().otp(otp).email(employee.getEmail()).expireTime(LocalDateTime.now().plusMinutes(5)).build()
        );

        //send mail
        String body = emailContentBuilder.buildEmail("forgotPassOtp",new OtpEmailDto(
               otp , employee.getName()
        ));
        emailService.sendEmailWithAttachment(
                new EmailSendingDto(
                        body , email,"Otp for forgot password !",null
                )
        );


    }

    @Transactional
    public void changePassword(String email, NewPasswordDto newPasswordDto) {
        //find employee
        Employee employee = employeeRepository.findByEmail(email).orElseThrow(
                () ->  new ResourceNotFoundException("Employee not found with this email id"));
        //find email otp by email
        EmailOtp emailOtp = emailOtpRepository.findByEmail(employee.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("otp not found for this mail.")
        );
        if( !emailOtp.getOtp().equals(newPasswordDto.getOtp()) ){
            throw new IllegalArgumentException("Otp is incorrect");
        }
        if( emailOtp.getExpireTime().isBefore(LocalDateTime.now()) ){
            throw new IllegalArgumentException("Otp is Expired!");
        }
        employee.setPasswordHash(passwordEncoder.encode(newPasswordDto.getNewPassword()));
        employeeRepository.save(employee);
    }
}
