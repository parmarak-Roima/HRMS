package com.HRMS.HRMS.dto.TravelDtos;
import com.HRMS.HRMS.entity.Enums.ExpenseStatus;
import lombok.Data;

@Data
public class TravelExpenseUpdateDto {
    private ExpenseStatus status;
    private String remarks;
}
