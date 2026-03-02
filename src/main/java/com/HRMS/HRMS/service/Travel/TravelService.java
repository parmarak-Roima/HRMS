package com.HRMS.HRMS.service.Travel;

import com.HRMS.HRMS.dto.AuthDtos.EmployeeIdEmailDto;
import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.EmailDtos.TravelEmailDto;
import com.HRMS.HRMS.dto.EmailDtos.EmailSendingDto;
import com.HRMS.HRMS.dto.TravelDtos.CreateTravelDto;
import com.HRMS.HRMS.dto.TravelDtos.ShowTravelDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Enums.TravelStatus;
import com.HRMS.HRMS.entity.JobEntities.JobOpening;
import com.HRMS.HRMS.entity.TravelEntities.Travel;
import com.HRMS.HRMS.entity.TravelEntities.TravelAssignment;
import com.HRMS.HRMS.exception.BadRequestException;
import com.HRMS.HRMS.exception.ForBiddenException;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.repository.TravelRepositories.TravelAssignmentRepo;
import com.HRMS.HRMS.repository.TravelRepositories.TravelRepository;
import com.HRMS.HRMS.service.Email.EmailContentBuilder;
import com.HRMS.HRMS.service.Email.EmailService;
import com.HRMS.HRMS.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class TravelService {

    private final TravelRepository travelRepository;

    private final EmployeeRepository employeeRepository;

    private final TravelAssignmentRepo travelAssignmentRepo;

    private final ModelMapper modelMapper;

    private final NotificationService notificationService;

    private final EmailContentBuilder emailContentBuilder;

    private final EmailService emailService;

    @Autowired
    public TravelService(TravelRepository travelRepository,
                         EmployeeRepository employeeRepository,
                         TravelAssignmentRepo travelAssignmentRepo,
                         ModelMapper modelMapper,
                         NotificationService notificationService,
                         EmailContentBuilder emailContentBuilder,
                         EmailService emailService){
        this.travelRepository = travelRepository;
        this.travelAssignmentRepo = travelAssignmentRepo;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
        this.notificationService = notificationService;
        this.emailContentBuilder = emailContentBuilder;
        this.emailService = emailService;
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
                if( isTravelExits(emp, travel.getStartDate() , travel.getEndDate() ) ){
                    throw new BadRequestException("Employee "+emp.getEmail()+" already have travel for this date");
                }
                assignment.setTravel(savedTravel);
                assignment.setEmployee(emp);
                assignment.setStartDate(savedTravel.getStartDate());
                assignment.setEndDate(savedTravel.getEndDate());
                assignment.setStatus(TravelStatus.SCHEDULED);
                travelAssignmentRepo.save(assignment);
                notificationService.sendNotification(
                    emp, "you are assigned to travel!!", "travel", user.getId()
                );
                sendMail(emp,travel);
            }
        }
        notificationService.sendNotification(
                hr , "you are created travel!!", "travel", travel.getId()
        );
        StringBuilder s = new StringBuilder("");
        for( int i = 0 ; i <  createTravelDto.getEmployeeIdsToAssign().toArray().length ; i++  ){
            s.append( createTravelDto.getEmployeeIdsToAssign().toArray()[i]);
        }
        log.info("trave(id:" + savedTravel.getId() + ") created by hr(email-id):-" + savedTravel.getCreatedBy().getEmail()
            + " for " + s.toString()
        );
        return modelMapper.map(savedTravel, CreateTravelDto.class);
    }

    private boolean isTravelExits(Employee emp, LocalDate startDate , LocalDate endDate) {
        List<TravelAssignment>  assignments  = travelAssignmentRepo.findByEmployeeId(emp.getId());
        return assignments.stream().anyMatch(
                (assignment)->{
                    boolean checkStartDate = (assignment.getStartDate().isAfter(startDate) || assignment.getStartDate().equals(startDate)) && (assignment.getStartDate().isBefore(endDate) || assignment.getStartDate().equals(endDate));
                    boolean checkEndDate =  (assignment.getEndDate().isAfter(startDate) || assignment.getEndDate().equals(startDate)) && (assignment.getEndDate().isBefore(endDate) || assignment.getEndDate().equals(endDate));
                    boolean checkOutOfRange = assignment.getStartDate().isAfter(startDate) && assignment.getEndDate().isBefore(endDate);
                    boolean checkInsideOfRange = assignment.getStartDate().isBefore(startDate) && assignment.getEndDate().isAfter(endDate);

                    return checkStartDate || checkEndDate || checkOutOfRange || checkInsideOfRange;
                }
        );
    }

    private void sendMail(Employee emp , Travel travel){
        String body = emailContentBuilder.buildEmail("travelAssign",new TravelEmailDto(
                emp.getName(), travel.getDestination() , travel.getStartDate(), travel.getEndDate(),travel.getDescription()
        ));
        emailService.sendEmailWithAttachment(
                new EmailSendingDto(
                        body , emp.getEmail(),"Travel assigned to you !",null
                )
        );
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
                authorized = true;
            }
            case "EMPLOYEE" -> {
                authorized = assignmentEmployeeIds.contains(user.getId());
            }
            case "MANAGER" -> {
                authorized = assignmentManagerIds.contains(user.getId()) || assignmentEmployeeIds.contains(user.getId());
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

    @Scheduled(cron = "0 0 8 * * *")
    public void markAsCompletedTravel(){
        travelRepository.updateStatus(TravelStatus.SCHEDULED,TravelStatus.COMPLETED,LocalDate.now());
    }
    private boolean isTravelExitsForUpdate(Travel travel, Employee emp, LocalDate startDate , LocalDate endDate) {
        List<TravelAssignment>  assignments  = travelAssignmentRepo.findByEmployeeId(emp.getId());
        return assignments.stream().anyMatch(
                (assignment)->{
                    if( assignment.getTravel().getId().equals( travel.getId()) ) return false;
                    boolean checkStartDate = (assignment.getTravel().getStartDate().isAfter(startDate) || assignment.getTravel().getStartDate().equals(startDate)) && (assignment.getTravel().getStartDate().isBefore(endDate) || assignment.getTravel().getStartDate().equals(endDate));
                    boolean checkEndDate =  (assignment.getTravel().getEndDate().isAfter(startDate) || assignment.getTravel().getEndDate().equals(startDate)) && (assignment.getTravel().getEndDate().isBefore(endDate) || assignment.getTravel().getEndDate().equals(endDate));
                    boolean checkOutOfRange = assignment.getTravel().getStartDate().isAfter(startDate) && assignment.getTravel().getEndDate().isBefore(endDate);
                    boolean checkInsideOfRange = assignment.getTravel().getStartDate().isBefore(startDate) && assignment.getTravel().getEndDate().isAfter(endDate);

                    return checkStartDate || checkEndDate || checkOutOfRange || checkInsideOfRange;
                }
        );
    }

    @Transactional
    public ShowTravelDto updateTravel(Long id, CreateTravelDto travelDto,CustomUserPrincipal user) {
        Travel existingTravel = travelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Travel Plan not found with ID: " + id));
        for (TravelAssignment travelAssignment : existingTravel.getTravelAssignments()) {
            if (isTravelExitsForUpdate(existingTravel, travelAssignment.getEmployee(), travelDto.getStartDate(), travelDto.getEndDate())) {
                throw new BadRequestException("Employee " + travelAssignment.getEmployee().getEmail() + " already have travel for this date");
            }
        }
        if( travelDto.getStatus() == TravelStatus.CANCELLED && existingTravel.getStartDate().isBefore(LocalDate.now()) ){
            throw new BadRequestException("you can not cancel travel after start date !");
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


