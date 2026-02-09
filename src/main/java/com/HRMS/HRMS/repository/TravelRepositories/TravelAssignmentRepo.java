package com.HRMS.HRMS.repository.TravelRepositories;

import com.HRMS.HRMS.entity.TravelEntities.TravelAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TravelAssignmentRepo extends JpaRepository<TravelAssignment,Long> {

    List<TravelAssignment> findByEmployeeId(Long employeeId);

    // Finds all assignments where the assigned employee reports to the given managerId
    @Query("SELECT ta FROM TravelAssignment ta WHERE ta.employee.manager.id = :managerId")
    List<TravelAssignment> findTeamTravels(Long managerId);

    boolean existsByTravelIdAndEmployeeId(Long travelId, Long employeeId);

}
