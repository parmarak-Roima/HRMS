package com.HRMS.HRMS.dto.EmailDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCancelledEmailDto {
    private String recipientName;
    private String gameName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String primaryBookerName;
}
