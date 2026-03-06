package com.HRMS.HRMS.repository;

import com.HRMS.HRMS.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp , Long> {

    Optional<EmailOtp> findByEmail(String email);

    void deleteByEmail(String email);
}
