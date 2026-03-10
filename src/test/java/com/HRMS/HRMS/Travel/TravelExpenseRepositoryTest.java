package com.HRMS.HRMS.Travel;

import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Enums.Designations;
import com.HRMS.HRMS.entity.Enums.ExpenseStatus;
import com.HRMS.HRMS.entity.Enums.TravelStatus;
import com.HRMS.HRMS.entity.TravelEntities.ExpenseType;
import com.HRMS.HRMS.entity.TravelEntities.Travel;
import com.HRMS.HRMS.entity.TravelEntities.TravelAssignment;
import com.HRMS.HRMS.entity.TravelEntities.TravelExpense;
import com.HRMS.HRMS.repository.TravelRepositories.TravelExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("Test")
class TravelExpenseRepositoryTest {
    @Autowired
    private TravelExpenseRepository travelExpenseRepository;
    @Autowired
    private TestEntityManager entityManager;

    private Employee employee;
    private Travel travel;
    private TravelAssignment assignment;
    private ExpenseType foodExpenseType;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        employee = new Employee();
        employee.setName("Rakesh");
        employee.setEmail("rakesh@roima.com");
        employee.setManager(null);
        employee.setDesignation(Designations.JR_SOFTWARE_DEVELOPER);
        employee.setProfileUrl("hello");
        employee.setPasswordHash("Alasdair");
        employee.setBirthdate(LocalDate.now());
        employee.setJoiningDate(LocalDate.now());
        entityManager.persist(employee);

        travel = new Travel();
        travel.setDestination("Mumbai");
        travel.setStartDate(LocalDate.now().plusDays(5));
        travel.setEndDate(LocalDate.now().plusDays(7));
        travel.setDescription("dsmds");
        travel.setRequiredDocs("asbds");
        travel.setStatus(TravelStatus.SCHEDULED);
        travel.setCreatedBy(employee);
        entityManager.persist(travel);

        assignment = new TravelAssignment();
        assignment.setEmployee(employee);
        assignment.setTravel(travel);
        assignment.setStartDate(LocalDate.now().plusDays(5));
        assignment.setEndDate(LocalDate.now().plusDays(7));
        assignment.setStatus(TravelStatus.SCHEDULED);
        entityManager.persist(assignment);
        foodExpenseType = new ExpenseType();
        foodExpenseType.setType("FOOD");
        entityManager.persist(foodExpenseType);

        // 1 APPROVED, Food,
        TravelExpense expense1 = new TravelExpense();
        expense1.setTravelAssignment(assignment);
        expense1.setExpenseType(foodExpenseType);
        expense1.setDate(today.plusDays(6));
        expense1.setAmount(new BigDecimal("500.00"));
        expense1.setStatus(ExpenseStatus.APPROVED);
        expense1.setCreatedAt(LocalDateTime.now().plusDays(6).minusHours(2)); // Older
        entityManager.persist(expense1);

        //2 PENDING, Food
        TravelExpense expense2 = new TravelExpense();
        expense2.setTravelAssignment(assignment);
        expense2.setExpenseType(foodExpenseType);
        expense2.setDate(today.plusDays(6));
        expense2.setAmount(new BigDecimal("300.00"));
        expense2.setStatus(ExpenseStatus.SUBMITTED);
        expense2.setCreatedAt(LocalDateTime.now().plusDays(6).minusHours(1)); // Newer
        entityManager.persist(expense2);

        //3 rejected
        TravelExpense expense3 = new TravelExpense();
        expense3.setTravelAssignment(assignment);
        expense3.setExpenseType(foodExpenseType);
        expense3.setDate(today.plusDays(6));
        expense3.setAmount(new BigDecimal("1000.00"));
        expense3.setStatus(ExpenseStatus.REJECTED);
        expense3.setCreatedAt(LocalDateTime.now().plusDays(6));
        entityManager.persist(expense3);
        entityManager.flush();
    }
    @Test
    void getDailyTotal_SumsAmountsExcludingRejected() {
        BigDecimal dailyTotal = travelExpenseRepository.getDailyTotal(
                employee.getId(), today.plusDays(6), foodExpenseType.getId());
        assertEquals(new BigDecimal("800.00"), dailyTotal);
    }
    @Test
    void getDailyTotal_WhenNoRecordsMatch() {
        //there is no record for after 7 days
        BigDecimal dailyTotal = travelExpenseRepository.getDailyTotal(
                employee.getId(), today.plusDays(7), foodExpenseType.getId());
        // should be zero
        assertEquals(new BigDecimal("0"), dailyTotal);
    }

    @Test
    void getTotalApprovedByTravelId() {
        // 1 is APPROVED
        BigDecimal totalApproved = travelExpenseRepository.getTotalApprovedByTravelId(travel.getId());
        // check
        assertEquals(new BigDecimal("500.00") ,totalApproved);
    }

    @Test
    void findTravelExpenseByTravelAssignment() {
        //call method
        List<TravelExpense> expenses = travelExpenseRepository
                .findTravelExpenseByTravelAssignment_IdOrderByCreatedAtDesc(assignment.getId());
        // check
        assertNotNull(expenses);
        assertEquals(3, expenses.size());
        assertEquals(ExpenseStatus.REJECTED, expenses.get(0).getStatus());
        assertEquals(ExpenseStatus.SUBMITTED, expenses.get(1).getStatus());
        assertEquals(ExpenseStatus.APPROVED, expenses.get(2).getStatus());
    }

}
