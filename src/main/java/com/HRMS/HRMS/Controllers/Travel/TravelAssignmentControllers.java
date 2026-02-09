package com.HRMS.HRMS.Controllers.Travel;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.TravelDtos.TravelAssignmentCreateDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelAssignmentResponseDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelAssignmentUpdateDto;
import com.HRMS.HRMS.service.TravelAssignmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<TravelAssignmentResponseDto>> assignEmployee(
            @Valid @RequestBody TravelAssignmentCreateDto dto) {
        TravelAssignmentResponseDto response = travelAssignmentService.createAssignment(dto);
        return ResponseEntity.ok(new ApiResponse<>("Employee assigned successfully", response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TravelAssignmentResponseDto>> updateAssignment(
            @PathVariable Long id,
            @RequestBody TravelAssignmentUpdateDto dto) {
        TravelAssignmentResponseDto response = travelAssignmentService.updateAssignment(id, dto);
        return ResponseEntity.ok(new ApiResponse<>("Assignment updated successfully", response));
    }

    @GetMapping("/employee/{empId}")
    public ResponseEntity<ApiResponse<List<TravelAssignmentResponseDto>>> getEmployeeTravels(
            @PathVariable Long empId) {
        List<TravelAssignmentResponseDto> list = travelAssignmentService.getEmployeeTravels(empId);
        return ResponseEntity.ok(new ApiResponse<>("Fetched employee travels", list));
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<ApiResponse<List<TravelAssignmentResponseDto>>> getTeamTravels(
            @PathVariable Long managerId) {
        List<TravelAssignmentResponseDto> list = travelAssignmentService.getTeamTravels(managerId);
        return ResponseEntity.ok(new ApiResponse<>("Fetched employee travels", list));
    }

}
