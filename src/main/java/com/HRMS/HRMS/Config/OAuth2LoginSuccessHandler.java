package com.HRMS.HRMS.Config;

import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.utils.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final EmployeeRepository employeeRepository;
    private final JwtUtils jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        //Check if this email exists in database
         Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));
        //Generate your standard JWT Token using your existing logic
        String jwtToken = jwtService.generateToken(employee.getEmail(),employee.getRole().getRole(),employee.getId(),employee.getName());
        //Redirect to the React frontend with the token in the URL
        String targetUrl = "http://localhost:5173" + "/oauth2/redirect?token=" + jwtToken;
        response.sendRedirect(targetUrl);
    }
}

