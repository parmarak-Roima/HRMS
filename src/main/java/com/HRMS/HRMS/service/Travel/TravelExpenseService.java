package com.HRMS.HRMS.service.Travel;
import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.EmailDtos.EmailSendingDto;
import com.HRMS.HRMS.dto.EmailDtos.ExpenseSubmittedEmailDto;
import com.HRMS.HRMS.dto.TravelDtos.ExpenseSummaryDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelExpenseCreateDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelExpenseResponseDto;
import com.HRMS.HRMS.dto.TravelDtos.TravelExpenseUpdateDto;
import com.HRMS.HRMS.entity.TravelEntities.*; // Your Entities
import com.HRMS.HRMS.entity.Enums.ExpenseStatus;
import com.HRMS.HRMS.exception.BadRequestException;
import com.HRMS.HRMS.exception.ForBiddenException;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.TravelRepositories.ExpenseTypeRepo;
import com.HRMS.HRMS.repository.TravelRepositories.TravelAssignmentRepo;
import com.HRMS.HRMS.repository.TravelRepositories.TravelExpenseRepository;
import com.HRMS.HRMS.service.DocumentService;
import com.HRMS.HRMS.service.Email.EmailContentBuilder;
import com.HRMS.HRMS.service.Email.EmailService;
import com.HRMS.HRMS.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@Slf4j
public class TravelExpenseService {

     private final TravelExpenseRepository expenseRepo;
     private final TravelAssignmentRepo assignmentRepo;
     private final ExpenseTypeRepo expenseTypeRepo;
     private final DocumentService documentService;
     private final ModelMapper modelMapper;
    private final NotificationService notificationService;
    private final EmailContentBuilder emailContentBuilder;
    private final EmailService emailService;

    @Autowired
    public TravelExpenseService(
            TravelExpenseRepository travelExpenseRepository,
            TravelAssignmentRepo travelAssignmentRepo,
            ExpenseTypeRepo expenseTypeRepo,
            DocumentService documentService,
            ModelMapper modelMapper,
            NotificationService notificationService,
            EmailContentBuilder emailContentBuilder,
            EmailService emailService
            ){
        this.expenseRepo = travelExpenseRepository;
        this.assignmentRepo = travelAssignmentRepo;
        this.expenseTypeRepo = expenseTypeRepo;
        this.documentService = documentService;
        this.modelMapper = modelMapper;
        this.notificationService = notificationService;
        this.emailContentBuilder = emailContentBuilder;
        this.emailService = emailService;
    }

    public TravelExpenseResponseDto createExpense(TravelExpenseCreateDto dto , CustomUserPrincipal user) {

        TravelAssignment assignment = assignmentRepo.findById(dto.getTravelAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Travel Assignment not found"));
        List<Long> assignmentEmployeeIds = assignment.getTravel().getTravelAssignments()
                .stream()
                .map(TravelAssignment -> {
                    return TravelAssignment.getEmployee().getId();
                })
                .toList();
        if(!assignmentEmployeeIds.contains(user.getId())){
            throw new ForBiddenException("You can not create expense for this travel !");
        }
        //Validations
        if (dto.getDate().isBefore(assignment.getStartDate())) {
            throw new BadRequestException("Expense date cannot be before assignment start date.");
        }
        if (dto.getDate().isAfter(assignment.getEndDate().plusDays(10))) {
            throw new BadRequestException("Expense submission window closed (10 days after assignment end).");
        }
        ExpenseType type = expenseTypeRepo.findById(dto.getExpenseTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense Type not found"));

        //daily limit validation
        if (type.getDailyLimit() != null && type.getDailyLimit() > 0) {
            Long empId = assignment.getEmployee().getId();
            BigDecimal currentTotal = expenseRepo.getDailyTotal(empId, dto.getDate(), type.getId());
            BigDecimal newTotal = currentTotal.add(dto.getAmount());
            BigDecimal dailyLimit = BigDecimal.valueOf(type.getDailyLimit());

            if (newTotal.compareTo(dailyLimit) > 0) {
                throw new BadRequestException("Daily limit exceeded for " + type.getType() +
                        ". Limit: " + dailyLimit + ", Used: " + currentTotal);
            }
        }
        String proofUrl = documentService.uploadFile(dto.getFile(), "expenses", assignment.getEmployee().getId(), false);
        TravelExpense expense = new TravelExpense();
        expense.setTravelAssignment(assignment);
        expense.setExpenseType(type);
        expense.setAmount(dto.getAmount());
        expense.setDate(dto.getDate());
        expense.setDescription(dto.getDescription());
        expense.setProofUrl(proofUrl);
        expense.setStatus(ExpenseStatus.DRAFT);

        //notification
        notificationService.sendNotification(
              assignment.getEmployee()   , "you uploaded expense for" +expense.getExpenseType().getType() + "with status" + expense.getStatus() , "travel-assignment", assignment.getId()
        );
        //logging
        log.info(
                "Employee" + expense.getTravelAssignment().getEmployee().getId() + " uploaded expense for" + expense.getExpenseType() + "with amount" + expense.getAmount() + "On Date"+expense.getDate()
                        + "(proof url :-"+expense.getProofUrl() + ")"
        );
        return mapToResponse(expenseRepo.save(expense));
    }

    public TravelExpenseResponseDto updateExpenseEmployee(Long expenseId, CustomUserPrincipal user) {
        TravelExpense expense = expenseRepo.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        List<Long> assignmentEmployeeIds = expense.getTravelAssignment().getTravel().getTravelAssignments()
                .stream()
                .map(travelAssignment -> {
                    return travelAssignment.getEmployee().getId();
                })
                .toList();
        if(!assignmentEmployeeIds.contains(user.getId())){
            throw new ForBiddenException("You can not create update this expense!");
        }
        if (expense.getStatus() == ExpenseStatus.APPROVED ) {
            throw new BadRequestException("Cannot edit expense that is Approved.");
        }
        expense.setStatus(ExpenseStatus.SUBMITTED);
        //notification
        //for hr
        notificationService.sendNotification(
                expense.getTravelAssignment().getTravel().getCreatedBy(),
                "Employee with email id"+ expense.getTravelAssignment().getEmployee().getEmail() + "submitted expense for travel ("+ expense.getTravelAssignment().getTravel().getId() + ")",
                "travel-expense",
                expenseId
        );
        log.info(
                "Employee with employee id:" + expense.getTravelAssignment().getEmployee().getId() + "submitted expense with expense id :-"+ expense.getId()
        );
        sendMail(expense);
        return mapToResponse(expenseRepo.save(expense));
    }

    private void sendMail(TravelExpense expense){
        String body = emailContentBuilder.buildEmail("travelAssign",new ExpenseSubmittedEmailDto(
                expense.getTravelAssignment().getTravel().getCreatedBy().getName(), expense.getExpenseType().getType() , expense.getTravelAssignment().getEmployee().getName(),expense.getTravelAssignment().getTravel().getDestination()
        ));
        emailService.sendEmailWithAttachment(
                new EmailSendingDto(
                        body , expense.getTravelAssignment().getTravel().getCreatedBy().getEmail(),"Expense submitted !",expense.getProofUrl()
                )
        );
    }

    public TravelExpenseResponseDto updateExpenseHR(Long expenseId, TravelExpenseUpdateDto dto,CustomUserPrincipal user) {
        TravelExpense expense = expenseRepo.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        if (dto.getStatus() == ExpenseStatus.REJECTED && (dto.getRemarks() == null || dto.getRemarks().isEmpty())) {
            throw new BadRequestException("Remarks are mandatory when rejecting.");
        }
        expense.setStatus(dto.getStatus());
        expense.setRemarks(dto.getRemarks());

        //notification
        //for employee
        notificationService.sendNotification(
                expense.getTravelAssignment().getEmployee() , "HR Changed status of expense" + expenseId +  "from SUBMITTED to" + expense.getStatus() , "travel-expense", expense.getId()
        );
        log.info(
                "Employee with employee id:" + expense.getTravelAssignment().getEmployee().getId() + " got changed status to" +  expense.getStatus() + "with expense id :-"+ expense.getId()
        );
        return mapToResponse(expenseRepo.save(expense));
    }

    public ExpenseSummaryDto getTravelTotals(Long travelId) {
        BigDecimal approved = expenseRepo.getTotalApprovedByTravelId(travelId);
        return new ExpenseSummaryDto("Total Approved", approved);
    }

    public BigDecimal getTotalExpenseForAssignment(Long assignmentId) {
        TravelAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        return expenseRepo.getTotalAmountByAssignmentId(assignmentId);
    }

    public List<ExpenseType> getAllExpenseType() {
        return expenseTypeRepo.findAll();
    }

    private TravelExpenseResponseDto mapToResponse(TravelExpense entity) {
        TravelExpenseResponseDto dto = modelMapper.map(entity, TravelExpenseResponseDto.class);
        dto.setTravelAssignmentId(entity.getTravelAssignment().getId());
        dto.setEmployeeName(entity.getTravelAssignment().getEmployee().getName());
        if (entity.getExpenseType() != null) {
            dto.setExpenseTypeId(entity.getExpenseType().getId());
            dto.setExpenseTypeName(entity.getExpenseType().getType());
        }
        return dto;
    }

    public List<TravelExpenseResponseDto> getExpenseByTravelAssignmentId(Long travelAssignmentId, CustomUserPrincipal user) {
        List<TravelExpense>  travelExpenses = expenseRepo.findTravelExpenseByTravelAssignment_IdOrderByCreatedAtDesc(travelAssignmentId);
        return travelExpenses.stream().map(
                travelExpense -> {
                    return mapToResponse(travelExpense);
                }
        ).toList();
    }
}