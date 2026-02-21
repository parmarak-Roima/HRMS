package com.HRMS.HRMS.repository.Game;

import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.GameEntities.GameInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameInterestRepository extends JpaRepository<GameInterest,Long> {
    GameInterest findGameInterestByGame_IdAndEmployee_Id(Long gameId , Long empID);

    List<GameInterest> findGameInterestByEmployee_Id(Long employeeId);

    List<GameInterest> findGameInterestByGameIdAndIsInterested(Long gameId,boolean isInterested);

    Long countGameInterestByGame_IdAndIsInterestedAndPlayedInCurrentCycle(Long gameId, boolean isInterested, int playedInCurrentCycle);

    @Modifying
    @Query("UPDATE GameInterest gi SET gi.playedInCurrentCycle = 0 WHERE gi.game.id = :gameId")
    void resetPlayedCountForGame(@Param("gameId") Long gameId);
}
