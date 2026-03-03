package com.HRMS.HRMS.repository;

import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository< Employee , Long> {
    Optional<Employee> findByEmail(String email);

    List<Employee> findByRole_Id(Long roleId);

    List<Employee> findByBirthdate(LocalDate today);

    List<Employee> findByJoiningDate(LocalDate today);
}
