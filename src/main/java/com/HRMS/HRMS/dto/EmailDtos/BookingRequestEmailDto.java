package com.HRMS.HRMS.dto.EmailDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class BookingRequestEmailDto {
    private String recipientName;
    private String gameName;
    private String status;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String primaryBookerName;
}
