package com.HRMS.HRMS.repository.Game;

import com.HRMS.HRMS.entity.GameEntities.GameSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GameSlotsRepository extends JpaRepository<GameSlot,Long> {
    List<GameSlot> findGameSlotByGame_IdAndDate(Long gameId, LocalDate date);

    List<GameSlot> findGameSlotByDate(LocalDate date);
}
