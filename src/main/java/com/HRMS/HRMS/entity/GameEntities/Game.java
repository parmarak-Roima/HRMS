package com.HRMS.HRMS.entity.GameEntities;

import com.HRMS.HRMS.entity.BaseEntity;
import com.HRMS.HRMS.entity.TravelEntities.Travel;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game",schema = "dbo")
@Data
public class Game extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalTime startTime;
    @Column(nullable = false)
    private LocalTime endTime;
    @Column(nullable = false)
    private int slotDuration; //(in minutes)
    @Column(nullable = false)
    private int minPlayers;
    @Column(nullable = false)
    private int maxPlayers;
    @Column(name = "current_cycle", columnDefinition = "INT DEFAULT 1")
    private int currentCycle;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameInterest> interestedGames = new ArrayList<>();

}
