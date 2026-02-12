package com.HRMS.HRMS.dto.TravelDtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Getter
@Setter
public class TravelExpenseCreateDto {
    @NotNull(message = "Travel ID is required")
    private Long travelAssignmentId;

    @NotNull(message = "Expense Type ID is required")
    private Long expenseTypeId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Expense date cannot be in the future")
    private LocalDate date;

    private String description;

    @NotNull(message = "File is required")
    private MultipartFile file;
}
