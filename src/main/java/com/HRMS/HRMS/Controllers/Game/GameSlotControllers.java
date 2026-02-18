package com.HRMS.HRMS.Controllers.Game;

import com.HRMS.HRMS.dto.ApiResponse;
import com.HRMS.HRMS.dto.GameDtos.GameSlotResponseDto;
import com.HRMS.HRMS.service.Game.GameSlotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/game/slot")
public class GameSlotControllers {

    private final GameSlotService gameSlotService;

    public GameSlotControllers(
            GameSlotService gameSlotService
    ){
        this.gameSlotService = gameSlotService;
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<ApiResponse<Void>> createSlotForNextDay(
            @PathVariable Long gameId
    ){
        gameSlotService.createGameSlotForDay(gameId);
        return new ResponseEntity<>(
                new ApiResponse<>(
                        "Slot created successFully", null
                ),HttpStatus.OK
        );
    }

    @GetMapping("/{gameId}/{date}")
    public ResponseEntity<ApiResponse<List<GameSlotResponseDto>>> getAllSlotsByDate(
                @PathVariable Long gameId,
                @PathVariable LocalDate date
    ){
        return new ResponseEntity<>(
                new ApiResponse<>(
                        "Slot fetched successFully", gameSlotService.getAllGameSlotsByDate(gameId , date)
                ),HttpStatus.OK
        );
    }

}
