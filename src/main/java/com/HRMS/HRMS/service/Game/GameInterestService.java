package com.HRMS.HRMS.service.Game;

import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.AuthDtos.EmployeeIdEmailDto;
import com.HRMS.HRMS.dto.GameDtos.GameInterestedResponseDto;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.GameEntities.GameInterest;
import com.HRMS.HRMS.repository.Game.GameInterestRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
public class GameInterestService {

    private final GameInterestRepository gameInterestRepository;

    @Autowired
    public GameInterestService(
            GameInterestRepository gameInterestRepository
    ){
        this.gameInterestRepository = gameInterestRepository;
    }

    public boolean toggleIsInterested(
            Long gameId , CustomUserPrincipal user
    ){
        GameInterest gameInterest = gameInterestRepository.findGameInterestByGame_IdAndEmployee_Id(gameId,user.getId());
        gameInterest.setInterested(!gameInterest.isInterested());
        gameInterestRepository.save(gameInterest);

        return gameInterest.isInterested();
    }

    public void updatePlayedInCurrentCycleCount(
            Long gameId , CustomUserPrincipal user,int count
    ){
        GameInterest gameInterest = gameInterestRepository.findGameInterestByGame_IdAndEmployee_Id(gameId,user.getId());
        gameInterest.setPlayedInCurrentCycle(count);
        gameInterestRepository.save(gameInterest);
    }

    public List<GameInterestedResponseDto> gameInterestByEmpId(CustomUserPrincipal user){
        List<GameInterest> gameInterests = gameInterestRepository.findGameInterestByEmployee_Id(user.getId());
        return gameInterests.stream().map(
                        gameInterest -> {
                            GameInterestedResponseDto gameInterestedResponseDto = new GameInterestedResponseDto();
                            gameInterestedResponseDto.setGameName(gameInterest.getGame().getName());
                            gameInterestedResponseDto.setGameId(gameInterest.getGame().getId());
                            gameInterestedResponseDto.setInterested(gameInterest.isInterested());
                            gameInterestedResponseDto.setPlayedInCurrentCycle(gameInterest.getPlayedInCurrentCycle());
                            return gameInterestedResponseDto;
                        }
                ).toList();
    }
    public List<EmployeeIdEmailDto> employeesInterestedByGameId(Long gameID){
        List<GameInterest> gameInterests = gameInterestRepository.findGameInterestByGameIdAndIsInterested(gameID,true);
        return gameInterests.stream().map(
                gameInterest ->
                     new EmployeeIdEmailDto(
                            gameInterest.getEmployee().getId(),
                            gameInterest.getEmployee().getEmail()
                    )
        ).toList();
    }

}
