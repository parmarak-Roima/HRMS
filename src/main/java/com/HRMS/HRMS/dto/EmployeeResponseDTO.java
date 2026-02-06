package com.HRMS.HRMS.dto;
import com.HRMS.HRMS.entity.Enums.Designations;
import com.HRMS.HRMS.entity.Enums.EmployeeRole;
import lombok.*;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Data
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private Designations designation;
    private String email;
    private String profileUrl;
    private EmployeeRole role;
    private LocalDate birthdate;
    private LocalDate joiningDate;
    private Long managerId;
}