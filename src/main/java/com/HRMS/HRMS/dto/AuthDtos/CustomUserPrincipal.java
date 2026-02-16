package com.HRMS.HRMS.dto.AuthDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomUserPrincipal {
    private Long id;
    private String email;
    private String name;
    private String role;
}
