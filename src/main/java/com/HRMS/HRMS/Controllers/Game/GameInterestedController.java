package com.HRMS.HRMS.Controllers.Game;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.AuthDtos.CustomUserPrincipal;
import com.HRMS.HRMS.dto.AuthDtos.EmployeeIdEmailDto;
import com.HRMS.HRMS.dto.GameDtos.GameInterestedResponseDto;
import com.HRMS.HRMS.service.Game.GameInterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game/interested")
public class GameInterestedController {

    private final GameInterestService gameInterestService;

    @Autowired
    public GameInterestedController(
            GameInterestService gameInterestService
    ){
        this.gameInterestService = gameInterestService;
    }

    @PatchMapping("/{gameId}")
    public ResponseEntity<ApiResponse<Void>> toggleIsInterested(
            @PathVariable Long gameId
    ){
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                gameInterestService.toggleIsInterested(gameId,user);
        return new ResponseEntity<>(
                    new ApiResponse<>(
                            "toggled interested successFully !!",null

                    ),HttpStatus.OK
                );
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<GameInterestedResponseDto>>> getAllGameInterest(){
        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return new ResponseEntity<>(
                new ApiResponse<>(
                        "fetched successFully !!",gameInterestService.gameInterestByEmpId(user)

                ),HttpStatus.OK
        );
    }
    @GetMapping("/{gameID}")
    public ResponseEntity<ApiResponse<List<EmployeeIdEmailDto>>> getAllGameInterest(
            @PathVariable Long gameID
    ){

        return new ResponseEntity<>(
                new ApiResponse<>(
                        "fetched successFully !!",gameInterestService.employeesInterestedByGameId(gameID)

                ),HttpStatus.OK
        );
    }
}
