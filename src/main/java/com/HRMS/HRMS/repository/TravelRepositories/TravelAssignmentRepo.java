package com.HRMS.HRMS.repository.TravelRepositories;

import com.HRMS.HRMS.entity.Enums.TravelStatus;
import com.HRMS.HRMS.entity.TravelEntities.TravelAssignment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TravelAssignmentRepo extends JpaRepository<TravelAssignment,Long> {

    List<TravelAssignment> findByEmployeeIdAndStatusIn(Long employeeId,List<TravelStatus> status);
    List<TravelAssignment> findByEmployeeId(Long employeeId);
    // Finds all assignments where the assigned employee reports to the given managerId
    @Query("SELECT ta FROM TravelAssignment ta WHERE ta.employee.manager.id = :managerId")
    List<TravelAssignment> findTeamTravels(Long managerId);

    boolean existsByTravelIdAndEmployeeId(Long travelId, Long employeeId);

    @Query("SELECT ta FROM TravelAssignment ta WHERE ta.travel.id = :travelId AND ta.employee.id = :empId")
    TravelAssignment findByTravelIdAndEmployeeId(Long travelId ,Long empId);

    @Transactional
    @Modifying
    @Query("UPDATE TravelAssignment t SET t.status = :newStatus WHERE t.endDate < :today AND t.status = :oldStatus ")
    void updateStatus(TravelStatus oldStatus , TravelStatus newStatus , LocalDate today);
}
