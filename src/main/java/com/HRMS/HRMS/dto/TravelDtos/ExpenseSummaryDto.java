package com.HRMS.HRMS.dto.TravelDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExpenseSummaryDto {
    private String category;
    private BigDecimal amount;
}
