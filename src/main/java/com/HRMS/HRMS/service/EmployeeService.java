package com.HRMS.HRMS.service;

import com.HRMS.HRMS.dto.EmployeeCreateDTO;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.RoleRepository;
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
    private final RoleRepository roleRepository;

    @Autowired
    public EmployeeService(
            ModelMapper modelMapper ,
            PasswordEncoder passwordEncoder ,
            EmployeeRepository employeeRepository,

            RoleRepository roleRepository){
        this.modelMapper  = modelMapper;
        this.passwordEncoder  = passwordEncoder;
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
    }

    public void createEmployee(EmployeeCreateDTO employeeCreateDTO){
        Employee employee = modelMapper.map(employeeCreateDTO, Employee.class);
        employee.setPasswordHash(passwordEncoder.encode(employee.getPasswordHash()));
        employee.setId(null);
        employee.setRole(roleRepository.getReferenceById(employeeCreateDTO.getRole_id()));
        employeeRepository.save(employee);

    }
    public Optional<Employee> findByEmail(String email){
        return employeeRepository.findByEmail(email);
    }
}
