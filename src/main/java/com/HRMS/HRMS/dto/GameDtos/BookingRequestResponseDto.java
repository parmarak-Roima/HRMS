package com.HRMS.HRMS.dto.GameDtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestResponseDto {
    private Long id ;

    private String status;

    private Long slotId;

    private LocalDateTime requestedAt;

    private Long primaryBookerId;

    private List<Long> participantsId;
}
