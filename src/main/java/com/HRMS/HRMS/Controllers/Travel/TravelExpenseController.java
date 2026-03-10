package com.HRMS.HRMS.Controllers.Travel;

import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.TravelDtos.ExpenseSummaryDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelExpenseCreateDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelExpenseResponseDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelExpenseUpdateDto;
import com.HRMS.HRMS.dto.*;
import com.HRMS.HRMS.entity.TravelEntities.ExpenseType;
import com.HRMS.HRMS.service.Travel.TravelExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class TravelExpenseController {

    private final TravelExpenseService travelExpenseService;
    @Autowired
    public TravelExpenseController(TravelExpenseService travelExpenseService){
        this.travelExpenseService = travelExpenseService;
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("@assignmentAuth.isAssigned(#dto.travelAssignmentId)")
    public ResponseEntity<ApiResponse<TravelExpenseResponseDto>> createExpense(
           @Valid TravelExpenseCreateDto dto) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TravelExpenseResponseDto response = travelExpenseService.createExpense(dto,user);
        return ResponseEntity.ok(new ApiResponse<>("Expense created successfully", response));
    }
    @GetMapping("/{travelAssignmentId}")
    @PreAuthorize("@assignmentAuth.canAccessExpense(#travelAssignmentId)")
    public ResponseEntity<ApiResponse<List<TravelExpenseResponseDto>>> getTravelExpenseByTravelAssignmentId(
            @PathVariable Long travelAssignmentId  ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<TravelExpenseResponseDto> response = travelExpenseService.getExpenseByTravelAssignmentId(travelAssignmentId,user);
        return ResponseEntity.ok(new ApiResponse<>("Travel expense for travel assignment !! ", response));
    }
    @PatchMapping(value = "/{travelExpenseId}/Employee")
    @PreAuthorize("hasRole('EMPLOYEE') AND @assignmentAuth.isExpenseCreatedBy(#travelExpenseId)")
    public ResponseEntity<ApiResponse<TravelExpenseResponseDto>> updateExpenseEmployee(
            @PathVariable Long travelExpenseId
            ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TravelExpenseResponseDto response = travelExpenseService.updateExpenseEmployee( travelExpenseId,user);
        return ResponseEntity.ok(new ApiResponse<>("Expense updated successfully", response));
    }

    @PatchMapping("/{travelExpenseId}/hr")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ApiResponse<TravelExpenseResponseDto>> updateExpenseHR(
            @PathVariable Long travelExpenseId,
            @RequestBody TravelExpenseUpdateDto dto
           ) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TravelExpenseResponseDto response = travelExpenseService.updateExpenseHR(travelExpenseId , dto,user);
        return ResponseEntity.ok(new ApiResponse<>("Expense status updated", response));
    }

    @GetMapping("/summary/{travelId}")
    public ResponseEntity<ApiResponse<ExpenseSummaryDto>> getTripSummary(@PathVariable Long travelId) {
        ExpenseSummaryDto summary = travelExpenseService.getTravelTotals(travelId);
        return ResponseEntity.ok(new ApiResponse<>("Expense summary fetched", summary));
    }
    @GetMapping("/employeeTtotal/{assignmentId}")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalExpense(
            @PathVariable Long assignmentId
            ) {
        BigDecimal total = travelExpenseService.getTotalExpenseForAssignment(assignmentId);
        return ResponseEntity.ok(new ApiResponse<>("Total expense amount fetched successfully", total));
    }
    @GetMapping("/expenseType")
    public ResponseEntity<ApiResponse<List<ExpenseType>>> getAllExpenseType(
    ) {
        List<ExpenseType> total = travelExpenseService.getAllExpenseType();
        return ResponseEntity.ok(new ApiResponse<>("All expense type !!", total));
    }
}
