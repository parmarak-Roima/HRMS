package com.HRMS.HRMS.service;

import com.HRMS.HRMS.dto.AuthDtos.EmployeeIdEmailDto;
import com.HRMS.HRMS.dto.userDtos.EmployeeCreateDTO;
import com.HRMS.HRMS.dto.userDtos.EmployeeResponseDTO;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<EmployeeIdEmailDto> allEmployee(){
        List<Employee> employees = employeeRepository.findAll();
        List<Employee> employeesRole = employees.stream()
                .filter(employee ->
                        employee.getRole().getRole().equals("EMPLOYEE")
                                || employee.getRole().getRole().equals("MANAGER") ).toList();
        return employeesRole.stream().map(
                employee -> {return new EmployeeIdEmailDto( employee.getId(),employee.getEmail() );}
        ).toList();
    }

    public EmployeeResponseDTO getEmployee(Long id){
       Employee employee = employeeRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("employee not found !!"));
        EmployeeResponseDTO employeeResponseDTO = modelMapper.map(employee,EmployeeResponseDTO.class);
        if (employee.getRole() != null) {
            employeeResponseDTO.setRole(employee.getRole().getRole());
        }
        if (employee.getManager() != null) {
            employeeResponseDTO.setManagerId(employee.getManager().getId());
            employeeResponseDTO.setMangerName(employee.getManager().getName());
        }
        return employeeResponseDTO;
    }

    public Optional<Employee> findByEmail(String email){
        return employeeRepository.findByEmail(email);
    }
}
