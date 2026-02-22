package com.HRMS.HRMS.repository.TravelRepositories;
import com.HRMS.HRMS.entity.TravelEntities.TravelExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TravelExpenseRepository extends JpaRepository<TravelExpense, Long> {

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM TravelExpense e " +
            "WHERE e.travelAssignment.employee.id = :empId " +
            "AND e.date = :date " +
            "AND e.expenseType.id = :typeId " +
            "AND e.status != 'REJECTED'")
    BigDecimal getDailyTotal(Long empId, LocalDate date, Long typeId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM TravelExpense e " +
            "WHERE e.travelAssignment.travel.id = :travelId AND e.status = 'APPROVED'")
    BigDecimal getTotalApprovedByTravelId(Long travelId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM TravelExpense e " +
            "WHERE e.travelAssignment.id = :assignmentId " +
            "AND e.status = 'APPROVED'")
    BigDecimal getTotalAmountByAssignmentId(Long assignmentId);

    List<TravelExpense> findTravelExpenseByTravelAssignment_IdOrderByCreatedAtDesc(Long travelAssignmentId);
}