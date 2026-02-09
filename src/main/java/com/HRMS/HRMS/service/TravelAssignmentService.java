package com.HRMS.HRMS.service;

import com.HRMS.HRMS.dto.*;
import com.HRMS.HRMS.dto.TravelDtos.ShowTravelDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelAssignmentCreateDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelAssignmentResponseDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelAssignmentUpdateDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Enums.TravelStatus;
import com.HRMS.HRMS.entity.TravelEntities.Travel;
import com.HRMS.HRMS.entity.TravelEntities.TravelAssignment;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.TravelRepositories.TravelAssignmentRepo;
import com.HRMS.HRMS.repository.TravelRepositories.TravelRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TravelAssignmentService {

     private final TravelAssignmentRepo assignmentRepo;
     private final TravelRepository travelRepo;
     private final EmployeeRepository employeeRepo;
     private final ModelMapper modelMapper;

    @Autowired
    public TravelAssignmentService(
            TravelAssignmentRepo travelAssignmentRepo,
            TravelRepository travelRepository,
            EmployeeRepository employeeRepository,
            ModelMapper modelMapper
    ){
        this.travelRepo = travelRepository;
        this.assignmentRepo = travelAssignmentRepo;
        this.modelMapper = modelMapper;
        this.employeeRepo = employeeRepository;
    }

    public TravelAssignmentResponseDto createAssignment(TravelAssignmentCreateDto dto) {
        //validations
        if (assignmentRepo.existsByTravelIdAndEmployeeId(dto.getTravelId(), dto.getEmployeeId())) {
            throw new IllegalArgumentException("Employee is already assigned to this trip.");
        }
        Travel travel = travelRepo.findById(dto.getTravelId())
                .orElseThrow(() -> new ResourceNotFoundException("Travel not found"));
        Employee employee = employeeRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        //create travel assignment
        TravelAssignment assignment = new TravelAssignment();
        assignment.setTravel(travel);
        assignment.setEmployee(employee);

        assignment.setStartDate( travel.getStartDate());
        assignment.setEndDate( travel.getEndDate());
        assignment.setStatus(TravelStatus.SCHEDULED);

        TravelAssignment saved = assignmentRepo.save(assignment);
        return mapToResponse(saved);
    }

    public TravelAssignmentResponseDto updateAssignment(Long id, TravelAssignmentUpdateDto dto) {
        TravelAssignment assignment = assignmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(" travel - Assignment not found!!!"));
        modelMapper.map(dto, assignment);
        TravelAssignment updated = assignmentRepo.save(assignment);
        return mapToResponse(updated);
    }

    public List<TravelAssignmentResponseDto> getEmployeeTravels(Long employeeId) {
        return assignmentRepo.findByEmployeeId(employeeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TravelAssignmentResponseDto> getTeamTravels(Long managerId) {
        return assignmentRepo.findTeamTravels(managerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //method for map (travel assignment does not have a field of travels)
    private TravelAssignmentResponseDto mapToResponse(TravelAssignment entity) {
        TravelAssignmentResponseDto dto = modelMapper.map(entity, TravelAssignmentResponseDto.class);
        dto.setTravelId(entity.getTravel().getId());
        dto.setDestination(entity.getTravel().getDestination());
        dto.setEmployeeId(entity.getEmployee().getId());
        dto.setEmployeeName(entity.getEmployee().getName());
        return dto;
    }
}


