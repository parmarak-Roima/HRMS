package com.HRMS.HRMS.service.Game;

import com.HRMS.HRMS.dto.GameDtos.GameSlotResponseDto;
import com.HRMS.HRMS.entity.GameEntities.Game;
import com.HRMS.HRMS.entity.GameEntities.GameSlot;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.Game.GameRepository;
import com.HRMS.HRMS.repository.Game.GameSlotsRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class GameSlotService {

    private final GameSlotsRepository gameSlotsRepository;
    private final GameRepository gameRepository;

    public GameSlotService(
            GameSlotsRepository gameSlotsRepository,
            GameRepository gameRepository
    ){
        this.gameSlotsRepository = gameSlotsRepository;
        this.gameRepository = gameRepository;
    }

    public void createGameSlotForDay(){
        List<Game> games = gameRepository.findAll();
        games.forEach(
                game -> {
                    if(game.getStartTime()==null){
                        throw new IllegalArgumentException("start time needed !!");

                    }
                    if(game.getEndTime()==null){
                        throw new IllegalArgumentException("end time needed !!");

                    }
                    if (game.getSlotDuration() <= 0) {
                        throw new IllegalArgumentException("Duration must be greater than zero");
                    }
                    if (!game.getStartTime().isBefore(game.getEndTime())) {
                        throw new IllegalArgumentException("Start time must be before end time");
                    }

                    LocalTime slotStart = game.getStartTime();

                    while (slotStart.plusMinutes(game.getSlotDuration()).isBefore(game.getEndTime()) ||
                            slotStart.plusMinutes(game.getSlotDuration()).equals(game.getEndTime())) {
                        LocalTime slotEnd = slotStart.plusMinutes(game.getSlotDuration());
                        gameSlotsRepository.save(new GameSlot(
                                game , LocalDate.now().plusDays(1),slotStart,slotEnd, GameSlot.SlotStatus.OPEN
                        ));
                        slotStart = slotEnd;
                    }
                }
        );
    }

    public List<GameSlotResponseDto> getAllGameSlotsByDate(LocalDate date) {
        List<GameSlot> gameSlotList = gameSlotsRepository.findGameSlotByDate(date);
        return gameSlotList.stream().map(
                gameSlot -> {
                    return new GameSlotResponseDto(
                            gameSlot.getId(),
                            gameSlot.getGame().getName(),
                            gameSlot.getGame().getId(),
                            gameSlot.getDate(),
                            gameSlot.getStartTime(),
                            gameSlot.getEndTime(),
                            gameSlot.getStatus().toString()
                    );

                }
        ).toList();
    }
}
