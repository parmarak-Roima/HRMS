package com.HRMS.HRMS.service;

import com.HRMS.HRMS.dto.EmployeeCreateDTO;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {
    private final ModelMapper modelMapper ;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(
            ModelMapper modelMapper ,
            PasswordEncoder passwordEncoder ,
            EmployeeRepository employeeRepository

    ){
        this.modelMapper  = modelMapper;
        this.passwordEncoder  = passwordEncoder;
        this.employeeRepository = employeeRepository;
    }

    public void createEmployee(EmployeeCreateDTO employeeCreateDTO){
        Employee employee = modelMapper.map(employeeCreateDTO, Employee.class);
        employee.setPasswordHash(passwordEncoder.encode(employee.getPasswordHash()));
        employee.setId(null);
        employeeRepository.save(employee);
    }
    public Optional<Employee> findByEmail(String email){
        return employeeRepository.findByEmail(email);
    }
}
