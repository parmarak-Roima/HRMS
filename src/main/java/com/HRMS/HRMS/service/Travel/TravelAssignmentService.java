package com.HRMS.HRMS.service.Travel;

import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.TravelDtos.TravelAssignmentCreateDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelAssignmentResponseDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelAssignmentUpdateDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Enums.TravelStatus;
import com.HRMS.HRMS.entity.TravelEntities.Travel;
import com.HRMS.HRMS.entity.TravelEntities.TravelAssignment;
import com.HRMS.HRMS.exception.BadRequestException;
import com.HRMS.HRMS.exception.ForBiddenException;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.TravelRepositories.TravelAssignmentRepo;
import com.HRMS.HRMS.repository.TravelRepositories.TravelRepository;
import com.HRMS.HRMS.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class TravelAssignmentService {

     private final TravelAssignmentRepo assignmentRepo;
     private final TravelRepository travelRepo;
     private final EmployeeRepository employeeRepo;
     private final ModelMapper modelMapper;
    private final NotificationService notificationService;
    private final TravelService travelService;

    @Autowired
    public TravelAssignmentService(
            TravelAssignmentRepo travelAssignmentRepo,
            TravelRepository travelRepository,
            EmployeeRepository employeeRepository,
            ModelMapper modelMapper,
            NotificationService notificationService,
            TravelService travelService){
        this.travelRepo = travelRepository;
        this.assignmentRepo = travelAssignmentRepo;
        this.modelMapper = modelMapper;
        this.employeeRepo = employeeRepository;
        this.notificationService = notificationService;
        this.travelService = travelService;
    }


    public TravelAssignmentResponseDto createAssignment(TravelAssignmentCreateDto dto, CustomUserPrincipal user) {
        Employee employee = employeeRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        //validations
        if (assignmentRepo.existsByTravelIdAndEmployeeId(dto.getTravelId(), dto.getEmployeeId())) {
            throw new IllegalArgumentException("Employee "+ employee.getEmail() +"  is already assigned to this trip.");
        }
        Travel travel = travelRepo.findById(dto.getTravelId())
                .orElseThrow(() -> new ResourceNotFoundException("Travel not found"));
        if(travelService.isTravelExits(employee, travel.getStartDate(), travel.getEndDate()) ){
            throw new BadRequestException("Employee "+employee.getEmail()+" already have travel for this date");
        }
        if(travel.getStatus() != TravelStatus.SCHEDULED){
            throw new BadRequestException("travel should be scheduled to assign new employee!!");
        }
        //create travel assignment
        TravelAssignment assignment = new TravelAssignment();
        assignment.setTravel(travel);
        assignment.setEmployee(employee);

        assignment.setStartDate( travel.getStartDate());
        assignment.setEndDate( travel.getEndDate());
        assignment.setStatus(TravelStatus.SCHEDULED);

        TravelAssignment saved = assignmentRepo.save(assignment);
        Employee hr = employeeRepo.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("HR Employee not found with ID: " + user.getId()));
        notificationService.sendNotification(
                hr , "you are assigned employee to travel with travel id" + saved.getTravel().getId()+ "employee email-id :" + saved.getEmployee().getEmail() ,
                "Travel"
                ,saved.getTravel().getId());
        notificationService.sendNotification(
                employee, "you are assigned to travel!!", "travel", user.getId()
        );
        travelService.sendAssignedMail(employee,travel);
        log.info("Travel"+"("+dto.getTravelId()+")" +" assigned for "+dto.getEmployeeId());
        return mapToResponse(saved);
    }

    public TravelAssignmentResponseDto updateAssignment(Long travelId,Long empId , TravelAssignmentUpdateDto dto) {
        TravelAssignment assignment = assignmentRepo.findByTravelIdAndEmployeeId(travelId,empId);
        if( assignment == null ){
            throw new ResourceNotFoundException("Assignment not found!!");
        }
        if( assignment.getStatus() == TravelStatus.CANCELLED ){
            throw new BadRequestException("Travel already assigned");
        }
        modelMapper.map(dto, assignment);
        TravelAssignment updated = assignmentRepo.save(assignment);

        return mapToResponse(updated);
    }

    public List<TravelAssignmentResponseDto> getEmployeeTravels(Long employeeId,CustomUserPrincipal user) {
        if( !user.getRole().equals("HR") ) {
            Employee employee = employeeRepo.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException(" employee not found!!!"));

            Long managerId = (employee.getManager() != null) ? employee.getManager().getId() : null;
            if (!user.getId().equals(employeeId) && !user.getId().equals(managerId)) {
                throw new ForBiddenException("You are not allowed to view this employee's travels");
            }
        }
        return assignmentRepo.findByEmployeeId(employeeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TravelAssignmentResponseDto> getTeamTravels(Long managerId,CustomUserPrincipal user) {
        if(!user.getId().equals(managerId)){
            throw new ForBiddenException("you can not access this team travels !!");
        }
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
        dto.setStatus(entity.getTravel().getStatus());
        dto.setStartDate(entity.getTravel().getStartDate());
        dto.setEndDate(entity.getTravel().getEndDate());
        return dto;
    }

    public Long getTravelAssignmentId(Long travelId, Long empId) {
         return assignmentRepo.findByTravelIdAndEmployeeId(travelId,empId).getId();
    }

//    @Scheduled(cron = "0 29 12 * * *")
    public void markAsCompletedTravelAssignment(){
        assignmentRepo.updateStatus(TravelStatus.SCHEDULED,TravelStatus.COMPLETED,LocalDate.now());
    }
}


