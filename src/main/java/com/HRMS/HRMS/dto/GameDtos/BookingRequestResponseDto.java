package com.HRMS.HRMS.dto.GameDtos;

import com.HRMS.HRMS.dto.AuthDtos.EmployeeIdEmailDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestResponseDto {
    private Long id ;

    private String requestStatus;

    private Long slotId;

    private String slotStatus;

    private String gameName;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalDateTime requestedAt;

    private Long primaryBookerId;

    private String primaryBookedEmailId;

    private List<EmployeeIdEmailDto> participants;
}
