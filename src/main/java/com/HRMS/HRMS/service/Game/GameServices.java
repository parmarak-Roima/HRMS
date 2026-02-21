package com.HRMS.HRMS.service.Game;

import com.HRMS.HRMS.dto.GameDtos.GameCreationDto;
import com.HRMS.HRMS.dto.GameDtos.GameResponseDto;
import com.HRMS.HRMS.dto.GameDtos.UpdateGameDto;
import com.HRMS.HRMS.entity.GameEntities.Game;
import com.HRMS.HRMS.exception.BadRequestException;
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

    public GameResponseDto updateGame(Long gameId , UpdateGameDto dto){
        Game existingGame = gameRepository.findById(gameId).orElseThrow( () ->
                new ResourceNotFoundException("game is not found for this id !!")
        );
        //validations for max and min player and end time and start time
        if( dto.getMaxPlayers() != null && dto.getMinPlayers() != null && dto.getMaxPlayers() < dto.getMinPlayers()) {
                throw new IllegalArgumentException("Minimum player should be less then Maximum Player");
            }
        if( dto.getStartTime() != null && dto.getEndTime() != null && dto.getStartTime().isAfter(dto.getEndTime())) {
                throw new IllegalArgumentException("start time should be before end time");
            }
        if( dto.getMaxPlayers() == null && dto.getMinPlayers() != null && existingGame.getMaxPlayers() < dto.getMinPlayers() ){
            throw new IllegalArgumentException("Minimum player should be less then Maximum Player");
        }
        if( dto.getMaxPlayers() != null && dto.getMinPlayers() == null && dto.getMaxPlayers() < existingGame.getMinPlayers() ){
            throw new IllegalArgumentException("Minimum player should be less then Maximum Player");
        }
        if( dto.getStartTime() == null && dto.getEndTime() != null && existingGame.getStartTime().isAfter(dto.getEndTime())) {
            throw new IllegalArgumentException("start time should be before end time");
        }
        if( dto.getStartTime() != null && dto.getEndTime() == null && dto.getStartTime().isAfter(existingGame.getEndTime())) {
            throw new IllegalArgumentException("start time should be before end time");
        }
        modelMapper.map(dto , existingGame);
        Game updatedGame = gameRepository.save(existingGame);
        return modelMapper.map( updatedGame,GameResponseDto.class );
    }

    public List<GameResponseDto> allGame() {

         return gameRepository.findAll().stream().map(
                game -> {
                    return modelMapper.map(game,GameResponseDto.class);
                }
        ).toList();
    }

    public GameResponseDto createGame(GameCreationDto gameCreationDto) {
        if( gameCreationDto.getMaxPlayers() < gameCreationDto.getMinPlayers()) {
            throw new IllegalArgumentException("Minimum player should be less then Maximum Player");
        }
        if(gameCreationDto.getStartTime().isAfter(gameCreationDto.getEndTime())) {
            throw new IllegalArgumentException("start time should be before end time");
        }
        Game game = gameRepository.save(modelMapper.map(gameCreationDto,Game.class));
        return modelMapper.map(game,GameResponseDto.class);
    }

    public GameResponseDto gameById(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(
                () ->       new ResourceNotFoundException("game not found")
        );
        return modelMapper.map(game,GameResponseDto.class);
    }
}
