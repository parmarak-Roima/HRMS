package com.HRMS.HRMS.dto.EmailDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseSubmittedEmailDto {
    private String hrName;
    private String expenseType;
    private String uploadedByName;
    private String destination;
}
