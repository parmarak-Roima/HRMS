package com.HRMS.HRMS.repository.Game;

import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.GameEntities.GameInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameInterestRepository extends JpaRepository<GameInterest,Long> {
    GameInterest findGameInterestByGame_IdAndEmployee_Id(Long gameId , Long empID);

    List<GameInterest> findGameInterestByEmployee_Id(Long employeeId);

    List<GameInterest> findGameInterestByGameIdAndIsInterested(Long gameId,boolean isInterested);
}
