package com.HRMS.HRMS.Controllers;

import com.HRMS.HRMS.dto.AuthDtos.AuthResponse;
import com.HRMS.HRMS.dto.AuthDtos.LoginRequest;
import com.HRMS.HRMS.dto.EmployeeCreateDTO;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.repository.RoleRepository;
import com.HRMS.HRMS.service.EmployeeService;
import com.HRMS.HRMS.utils.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final EmployeeService employeeService;


    private final JwtUtils jwtUtils;

    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public AuthController(
            EmployeeService employeeService,
            JwtUtils jwtUtils,
            AuthenticationManager authenticationManager,
            RoleRepository roleRepository){
        this.employeeService = employeeService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid EmployeeCreateDTO employeeCreateDTO) {
        if(employeeService.findByEmail(employeeCreateDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        employeeService.createEmployee(employeeCreateDTO);

        return ResponseEntity.ok("Created SuccessFully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {

            //checks email & password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // If valid, fetch the employee details to put in the token
            if (authentication.isAuthenticated()) {
                Employee employee = employeeService.findByEmail(request.getEmail()).get();

                String token = jwtUtils.generateToken(
                        employee.getEmail(),
                        employee.getRole().getRole(),
                        employee.getId(),
                        employee.getName()
                );
                return ResponseEntity.ok(new AuthResponse(token));
            } else {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
    }
}
