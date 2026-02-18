package com.HRMS.HRMS.service.Game;

import com.HRMS.HRMS.dto.GameDtos.UpdateGameDto;
import com.HRMS.HRMS.entity.GameEntities.Game;
import com.HRMS.HRMS.exception.ResourceNotFoundException;
import com.HRMS.HRMS.repository.Game.GameRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
public class GameServices {
    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public GameServices(GameRepository gameRepository, ModelMapper modelMapper){
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
    }

    public Game updateGame(Long gameId , UpdateGameDto dto){
        Game existingGame = gameRepository.findById(gameId).orElseThrow( () ->
                new ResourceNotFoundException("game is not found for this id !!")
        );
        modelMapper.map(dto , existingGame);
        Game updatedGame = gameRepository.save(existingGame);
        return modelMapper.map( updatedGame,Game.class );
    }

    public List<Game> allGame() {
        return gameRepository.findAll();
    }
}
