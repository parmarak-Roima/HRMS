package com.HRMS.HRMS.Controllers.Game;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.GameDtos.GameCreationDto;
import com.HRMS.HRMS.dto.GameDtos.GameResponseDto;
import com.HRMS.HRMS.dto.GameDtos.UpdateGameDto;
import com.HRMS.HRMS.entity.GameEntities.Game;
import com.HRMS.HRMS.service.Game.GameServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game/config")

public class GameConfigController {
    private final GameServices gameServices;
    @Autowired
    public GameConfigController(GameServices gameServices){
        this.gameServices = gameServices;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<GameResponseDto>>> allGame(){
        return new ResponseEntity<>(
                new ApiResponse<
                        >(
                        "Games fetched SuccessFully !!!" , gameServices.allGame()
                ), HttpStatus.OK);
    }

    @PatchMapping("/{gameID}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ApiResponse<GameResponseDto>> updateGame(
            @PathVariable Long gameID,
            @RequestBody  UpdateGameDto dto){
        return new ResponseEntity<>(
                new ApiResponse<
                        >(
                    "Game Updated SuccessFully !!!" , gameServices.updateGame(gameID , dto)
                ), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ApiResponse<GameResponseDto>> createGame(@RequestBody GameCreationDto gameCreationDto) {
        return new ResponseEntity<>(
                new ApiResponse<>("Game Created Successfully", gameServices.createGame(gameCreationDto)),
                HttpStatus.CREATED
        );
    }

}
