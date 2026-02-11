package com.HRMS.HRMS.dto.AuthDtos;

import com.HRMS.HRMS.dto.CustomUserPrincipal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private CustomUserPrincipal user;
}
