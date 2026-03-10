package com.HRMS.HRMS.service.Auth;

import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.entity.TravelEntities.Travel;
import com.HRMS.HRMS.entity.TravelEntities.TravelAssignment;
import com.HRMS.HRMS.entity.TravelEntities.TravelExpense;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.TravelRepositories.TravelAssignmentRepo;
import com.HRMS.HRMS.repository.TravelRepositories.TravelExpenseRepository;
import com.HRMS.HRMS.repository.TravelRepositories.TravelRepository;
import com.HRMS.HRMS.service.Travel.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service("assignmentAuth")
public class AssignmentAuthorizationService {

    private final TravelExpenseRepository expenseRepository;
    private final TravelAssignmentRepo assignmentRepo;
    private final TravelRepository travelRepository;

    @Autowired
    public AssignmentAuthorizationService(TravelAssignmentRepo travelAssignmentRepo,TravelExpenseRepository travelExpenseRepository,TravelRepository travelRepository){
        this.assignmentRepo = travelAssignmentRepo;
        this.expenseRepository = travelExpenseRepository;
        this.travelRepository = travelRepository;
    }

    public boolean isAssigned( Long assignmentId ){
        TravelAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel Assignment not found"));

        return assignment.getEmployee().getId().equals(getCurrentUserId());
    }

    public boolean isExpenseCreatedBy( Long expenseId ){
        TravelExpense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel expense not found"));

        return expense.getTravelAssignment().getEmployee().getId().equals(getCurrentUserId());
    }

    public boolean canAccessExpense(Long assignmentId){
        String role = getRole();
        if( role.equals("EMPLOYEE") ) {
            return isAssigned(assignmentId);
        } else if (role.equals("MANAGER")) {
            TravelAssignment assignment = assignmentRepo.findById(assignmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Travel Assignment not found"));
            return assignment.getEmployee().getManager().getId().equals(getCurrentUserId());
        } else{
            return true;
        }
    }

    public boolean canAccessTravel(Long travelId){
        //take a travel
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel Plan not found with ID: " + travelId));
        List<Long> assignmentEmployeeIds;
        if( travel.getTravelAssignments() == null ){
            assignmentEmployeeIds = new ArrayList<>();
        }else {
            //take all assignment
            assignmentEmployeeIds = travel.getTravelAssignments()
                    .stream()
                    .map(TravelAssignment -> {
                        return TravelAssignment.getEmployee().getId();
                    })
                    .toList();
        }
        String role = getRole();
        if( role.equals("EMPLOYEE") ) {
            //check that user assigned to the travel or not
            return assignmentEmployeeIds.contains(getCurrentUserId());
        } else if (role.equals("MANAGER")) {
            List<Long> assignmentManagerIds;
            if( travel.getTravelAssignments() == null ){
                assignmentManagerIds = new ArrayList<>();
            }else {
                assignmentManagerIds = travel.getTravelAssignments()
                        .stream()
                        .map(ta -> {
                            if (ta.getEmployee().getManager() != null) {
                                return ta.getEmployee().getManager().getId();
                            }
                            return null;
                        }).filter(Objects::nonNull)
                        .toList();
            }
            return assignmentManagerIds.contains(getCurrentUserId()) || assignmentEmployeeIds.contains(getCurrentUserId());
        } else{
            return true;
        }
    }

    private String getRole() {
        return ((CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRole();
    }
    private Long getCurrentUserId() {
        return ((CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

}
