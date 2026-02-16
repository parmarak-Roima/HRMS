package com.HRMS.HRMS.dto.userDtos;
import com.HRMS.HRMS.entity.Enums.Designations;
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
    private String role;
    private LocalDate birthdate;
    private LocalDate joiningDate;
    private Long managerId;
    private String mangerName;
}