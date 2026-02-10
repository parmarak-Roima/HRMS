package com.HRMS.HRMS.dto;
import com.HRMS.HRMS.entity.Enums.Designations;
import com.HRMS.HRMS.entity.Enums.EmployeeRole;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeCreateDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Designation is required")
    private Designations designation;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 255, message = "Profile URL must be less than 255 characters")
    private String profileUrl;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String passwordHash; // Plain password for creation

    @NotNull(message = "Role is required")
    private Long role_id;

    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past")
    private LocalDate birthdate;

    @NotNull(message = "Joining date is required")
    @PastOrPresent(message = "Joining date cannot be in the future")
    private LocalDate joiningDate;

    private Long managerId;
}
