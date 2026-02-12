package com.HRMS.HRMS.dto.TravelDtos;

import com.HRMS.HRMS.entity.Enums.ExpenseStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TravelExpenseResponseDto {
    private Long id;
    private Long travelAssignmentId;
    private String employeeName;
    private Long expenseTypeId;
    private String expenseTypeName;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private String proofUrl;
    private ExpenseStatus status;
    private String remarks;
}

