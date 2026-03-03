package com.HRMS.HRMS.Controllers.Travel;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.TravelDtos.TravelAssignmentCreateDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelAssignmentResponseDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelAssignmentUpdateDto;
import com.HRMS.HRMS.service.Travel.TravelAssignmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
public class TravelAssignmentControllers {

    private final TravelAssignmentService travelAssignmentService;

    @Autowired
    public TravelAssignmentControllers( TravelAssignmentService travelAssignmentService ){
        this.travelAssignmentService = travelAssignmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ApiResponse<TravelAssignmentResponseDto>> assignEmployee(
            @Valid @RequestBody TravelAssignmentCreateDto dto) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TravelAssignmentResponseDto response = travelAssignmentService.createAssignment(dto,user);
        return ResponseEntity.ok(new ApiResponse<>("Employee assigned successfully", response));
    }

    @PatchMapping("/{travelId}/{empId}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ApiResponse<TravelAssignmentResponseDto>> updateAssignment(
            @PathVariable Long travelId,
            @PathVariable Long empId,
            @RequestBody TravelAssignmentUpdateDto dto) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TravelAssignmentResponseDto response = travelAssignmentService.updateAssignment(travelId,empId, dto);
        return ResponseEntity.ok(new ApiResponse<>("Assignment updated successfully", response));
    }

    @GetMapping("/employee/{empId}")
    public ResponseEntity<ApiResponse<List<TravelAssignmentResponseDto>>> getEmployeeTravels(
            @PathVariable Long empId) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<TravelAssignmentResponseDto> list = travelAssignmentService.getEmployeeTravels(empId,user);
        return ResponseEntity.ok(new ApiResponse<>("Fetched employee travels", list));
    }

    @GetMapping("/manager/{managerId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<TravelAssignmentResponseDto>>> getTeamTravels(
            @PathVariable Long managerId) {
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<TravelAssignmentResponseDto> list = travelAssignmentService.getTeamTravels(managerId,user);
        return ResponseEntity.ok(new ApiResponse<>("Fetched employee travels", list));
    }
    @GetMapping("/travelAssignmentId/{travelId}/{empId}")
    public ResponseEntity<ApiResponse<Long>> getTravelAssignmentId(
            @PathVariable Long travelId , @PathVariable Long empId
    ){
        Long travelAssignmentID = travelAssignmentService.getTravelAssignmentId(travelId,empId);
        return ResponseEntity.ok(new ApiResponse<>("travel assignment id fetched sucessfully",travelAssignmentID));
    }
}
