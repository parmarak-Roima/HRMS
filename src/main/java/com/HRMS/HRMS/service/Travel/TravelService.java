package com.HRMS.HRMS.service.Travel;

import com.HRMS.HRMS.dto.AuthDtos.EmployeeIdEmailDto;
import com.HRMS.HRMS.dto.CustomUserPrincipal;
import com.HRMS.HRMS.dto.TravelDtos.CreateTravelDto;
import com.HRMS.HRMS.dto.TravelDtos.ShowTravelDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Enums.TravelStatus;
import com.HRMS.HRMS.entity.TravelEntities.Travel;
import com.HRMS.HRMS.entity.TravelEntities.TravelAssignment;
import com.HRMS.HRMS.exception.ForBiddenException;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.TravelRepositories.TravelAssignmentRepo;
import com.HRMS.HRMS.repository.TravelRepositories.TravelRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class TravelService {

    private final TravelRepository travelRepository;

    private final EmployeeRepository employeeRepository;

    private final TravelAssignmentRepo travelAssignmentRepo;

    private final ModelMapper modelMapper;

    @Autowired
    public TravelService(TravelRepository travelRepository,
                         EmployeeRepository employeeRepository,
                         TravelAssignmentRepo travelAssignmentRepo,
                         ModelMapper modelMapper){
        this.travelRepository = travelRepository;
        this.travelAssignmentRepo = travelAssignmentRepo;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CreateTravelDto createTravel(CreateTravelDto createTravelDto, CustomUserPrincipal user){
        if (createTravelDto.getEndDate().isBefore(createTravelDto.getStartDate())) {
            throw new IllegalArgumentException("End Date cannot be before Start Date");
        }
        Travel travel = modelMapper.map(createTravelDto, Travel.class);
        travel.setId(null);
        if (travel.getStatus() == null) {
            travel.setStatus(TravelStatus.SCHEDULED);
        }
        Employee hr = employeeRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("HR Employee not found with ID: " + user.getId()));
        travel.setCreatedBy(hr);

        Travel savedTravel = travelRepository.save(travel);

        if (createTravelDto.getEmployeeIdsToAssign() != null && !createTravelDto.getEmployeeIdsToAssign().isEmpty()) {
            List<Employee> employees = employeeRepository.findAllById(createTravelDto.getEmployeeIdsToAssign());

            for (Employee emp : employees) {
                TravelAssignment assignment = new TravelAssignment();
                assignment.setTravel(savedTravel);
                assignment.setEmployee(emp);
                assignment.setStartDate(savedTravel.getStartDate());
                assignment.setEndDate(savedTravel.getEndDate());
                assignment.setStatus(TravelStatus.SCHEDULED);
                travelAssignmentRepo.save(assignment);
                /// TODO: emailService.sendTravelAssignedEmail(emp, savedTravel);
            }
        }
        return modelMapper.map(savedTravel, CreateTravelDto.class);
    }

    public List<ShowTravelDto> getAllTravels() {
        return travelRepository.findAll().stream()
                .map(travel -> {
                    ShowTravelDto dto = modelMapper.map(travel, ShowTravelDto.class);
                    if (dto.getEmployeeIdsToAssign() == null) {
                        dto.setEmployeeIdsToAssign(new ArrayList<>());
                    }
                    travel.getTravelAssignments().forEach(assignment ->
                            dto.getEmployeeIdsToAssign().add( new EmployeeIdEmailDto( assignment.getEmployee().getId(),assignment.getEmployee().getEmail()))
                    );
                    dto.setCreated_by_id(travel.getCreatedBy().getId());
                    return dto;
                })
                .collect(toList());
    }

    public ShowTravelDto getTravelById(Long id,CustomUserPrincipal user) {
        //find travel by id
        Travel travel = travelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Travel Plan not found with ID: " + id));
        //validate users
        boolean authorized = false;
        List<Long> assignmentEmployeeIds = travel.getTravelAssignments()
                .stream()
                .map(TravelAssignment -> {
                    return TravelAssignment.getEmployee().getId();
                })
                .toList();
        List<Long> assignmentManagerIds = travel.getTravelAssignments()
                .stream()
                .map(ta -> {
                    if (ta.getEmployee().getManager() != null) {
                        return ta.getEmployee().getManager().getId();
                    }
                    return null;
                }) .filter(Objects::nonNull)
                .toList();
        switch (user.getRole()) {
            case "HR" -> {
                authorized = travel.getCreatedBy().getId().equals(user.getId());
            }
            case "EMPLOYEE" -> {
                authorized = assignmentEmployeeIds.contains(user.getId());
            }
            case "MANAGER" -> {
                authorized = assignmentManagerIds.contains(user.getId());
            }
            default -> authorized = false; // No access for other roles
        }
        if (!authorized) {
            throw new ForBiddenException("You are not authorized to see this travel!");
        }
        //return data
        ShowTravelDto travelDto =  modelMapper.map(travel, ShowTravelDto.class);

        if (travelDto.getEmployeeIdsToAssign() == null) {
            travelDto.setEmployeeIdsToAssign(new ArrayList<>());
        }

        travel.getTravelAssignments().forEach(assignment ->
                travelDto.getEmployeeIdsToAssign().add( new EmployeeIdEmailDto( assignment.getEmployee().getId(),assignment.getEmployee().getEmail()))
        );
        travelDto.setCreated_by_id(travel.getCreatedBy().getId());
        return travelDto;
    }

    @Transactional
    public ShowTravelDto updateTravel(Long id, CreateTravelDto travelDto,CustomUserPrincipal user) {
        Travel existingTravel = travelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Travel Plan not found with ID: " + id));
        //check if this travel is created by hr or not
        if( user.getRole().equals("HR") && !existingTravel.getCreatedBy().getId().equals(user.getId()) ){
            throw new ForBiddenException("you are not authorized to update this travel !!") ;
        }
        modelMapper.map(travelDto, existingTravel);
        if (existingTravel.getEndDate().isBefore(existingTravel.getStartDate())) {
            throw new IllegalArgumentException("End Date cannot be before Start Date");
        }

        Travel updatedTravel = travelRepository.save(existingTravel);
        ShowTravelDto showTravelDto =  modelMapper.map(updatedTravel, ShowTravelDto.class);
        showTravelDto.setCreated_by_id(existingTravel.getCreatedBy().getId());
        return showTravelDto;
    }

    public List<ShowTravelDto> getTravelForHr(CustomUserPrincipal user){
        List<Travel> travels = travelRepository.findByCreatedById(user.getId());
        return travels.stream()
                .map(travel -> {
                    ShowTravelDto dto = modelMapper.map(travel, ShowTravelDto.class);
                    if (dto.getEmployeeIdsToAssign() == null) {
                        dto.setEmployeeIdsToAssign(new ArrayList<>());
                    }
                    travel.getTravelAssignments().forEach(assignment ->
                            dto.getEmployeeIdsToAssign().add( new EmployeeIdEmailDto( assignment.getEmployee().getId(),assignment.getEmployee().getEmail()))
                    );
                    dto.setCreated_by_id(travel.getCreatedBy().getId());
                    return dto;
                })
                .collect(toList());
    }
}


