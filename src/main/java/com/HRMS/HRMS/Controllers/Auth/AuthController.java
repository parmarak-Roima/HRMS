package com.HRMS.HRMS.Controllers.Auth;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.AuthDtos.AuthResponse;
import com.HRMS.HRMS.dto.AuthDtos.EmployeeIdEmailDto;
import com.HRMS.HRMS.dto.AuthDtos.LoginRequest;
import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.userDtos.EmployeeCreateDTO;
import com.HRMS.HRMS.dto.userDtos.EmployeeResponseDTO;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.repository.RoleRepository;
import com.HRMS.HRMS.service.EmployeeService;
import com.HRMS.HRMS.utils.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {

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
                CustomUserPrincipal user = new CustomUserPrincipal(employee.getId(), employee.getEmail(),employee.getName(),employee.getRole().getRole());
                return ResponseEntity.ok( (new AuthResponse(token,user)));
            } else {
                throw new BadCredentialsException("Invalid credentials");
            }
    }
    @GetMapping("/employee/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable long id){
        return new ResponseEntity<>(
                new ApiResponse<>("user data",employeeService.getEmployee(id) ),
                HttpStatus.OK
        );
    }
    @GetMapping
    public ResponseEntity<ApiResponse<CustomUserPrincipal>> profile() {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(
                new ApiResponse<>("user data",user ),
                HttpStatus.OK
        );
    }

    @GetMapping("/employee")
    public ResponseEntity<ApiResponse<List<EmployeeIdEmailDto>>> allEmployee() {
        return new ResponseEntity<>(
                new ApiResponse<>("user data", employeeService.allEmployee()),
                HttpStatus.OK
        );
    }

    @GetMapping("/hr")
    public ResponseEntity<ApiResponse<List<EmployeeIdEmailDto>>> allhrs() {
        return new ResponseEntity<>(
                new ApiResponse<>("user data",employeeService.allHrs() ),
                HttpStatus.OK
        );
    }

    @GetMapping("/birthday")
    public ResponseEntity<ApiResponse<List<EmployeeIdEmailDto>>> allBirthDayEmployee() {
        return new ResponseEntity<>(
                new ApiResponse<>("user data",employeeService.allBirthDayEmployee() ),
                HttpStatus.OK
        );
    }

    @GetMapping("/joiningAniversary")
    public ResponseEntity<ApiResponse<List<EmployeeIdEmailDto>>> allJoiningDayEmployee() {
        return new ResponseEntity<>(
                new ApiResponse<>("user data",employeeService.allJoiningDayEmployee() ),
                HttpStatus.OK
        );
    }

}
