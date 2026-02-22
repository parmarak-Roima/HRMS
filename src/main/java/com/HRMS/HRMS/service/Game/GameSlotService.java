package com.HRMS.HRMS.service.Game;

import com.HRMS.HRMS.dto.GameDtos.GameSlotResponseDto;
import com.HRMS.HRMS.entity.GameEntities.Game;
import com.HRMS.HRMS.entity.GameEntities.GameSlot;
import com.HRMS.HRMS.exception.BadRequestException;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.Game.GameRepository;
import com.HRMS.HRMS.repository.Game.GameSlotsRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

//    @Scheduled(cron = "0 28 21 * * *")
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
                    LocalDate date = LocalDate.now().plusDays(1);
                    LocalTime slotStart = game.getStartTime();

                    while (slotStart.plusMinutes(game.getSlotDuration()).isBefore(game.getEndTime()) ||
                            slotStart.plusMinutes(game.getSlotDuration()).equals(game.getEndTime())) {
                        LocalTime slotEnd = slotStart.plusMinutes(game.getSlotDuration());

                        gameSlotsRepository.save(new GameSlot(
                                game ,date ,slotStart,slotEnd, GameSlot.SlotStatus.OPEN
                        ));
                        slotStart = slotEnd;
                    }
                }
        );
        log.info("slot generation process finished for tommorow!");
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

    public List<GameSlotResponseDto> getGameSlotsByGameIdAndDate(Long gameId, LocalDate date) {
        List<GameSlot> slots = gameSlotsRepository.findGameSlotByGame_IdAndDate(gameId, date);

        return slots.stream().map(slot -> new GameSlotResponseDto(
                slot.getId(),
                slot.getGame().getName(),
                slot.getGame().getId(),
                slot.getDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus().toString()
        )).toList();
    }


    public List<GameSlotResponseDto> getUpcomingGameSlots(Long gameId) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<GameSlot> todaySlots = gameSlotsRepository.findGameSlotByGame_IdAndDate(gameId,today);
        List<GameSlot> tomorrowSlots = gameSlotsRepository.findGameSlotByGame_IdAndDate(gameId , tomorrow);

        List<GameSlot> allSlots = new ArrayList<>(todaySlots);
        allSlots.addAll(tomorrowSlots);

        LocalDateTime thresholdDateTime = LocalDateTime.now().plusMinutes(45);

        return allSlots.stream()
                .filter(gameSlot -> {

                    LocalDateTime slotDateTime = LocalDateTime.of(gameSlot.getDate(), gameSlot.getStartTime());

                    return slotDateTime.isAfter(thresholdDateTime);
                })
                .map(gameSlot -> new GameSlotResponseDto(
                        gameSlot.getId(),
                        gameSlot.getGame().getName(),
                        gameSlot.getGame().getId(),
                        gameSlot.getDate(),
                        gameSlot.getStartTime(),
                        gameSlot.getEndTime(),
                        gameSlot.getStatus().toString()
                ))
                .toList();
    }
}
