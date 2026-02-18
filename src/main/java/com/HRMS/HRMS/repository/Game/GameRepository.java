package com.HRMS.HRMS.repository.Game;

import com.HRMS.HRMS.entity.GameEntities.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game,Long> {
}
