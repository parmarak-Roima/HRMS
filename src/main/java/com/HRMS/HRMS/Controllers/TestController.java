package com.HRMS.HRMS.Controllers;
import com.HRMS.HRMS.dto.AuthDtos.LoginRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @GetMapping
    @PreAuthorize("hasRole('HR') or hasRole('EMPLOYEE')")
    public String getUserById(@RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "Validation Failed: " + bindingResult.getAllErrors();
        }
        log.info("listening form test");
        return "hello world";
    }
}
