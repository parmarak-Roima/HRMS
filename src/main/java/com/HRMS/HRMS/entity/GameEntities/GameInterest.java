package com.HRMS.HRMS.entity.GameEntities;

import com.HRMS.HRMS.entity.Employee;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "game_interest",schema = "dbo")
@Data
public class GameInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(columnDefinition = "BIT DEFAULT false")
    private boolean isInterested;

    @Column(name = "played_in_current_cycle", columnDefinition = "INT DEFAULT 0")
    private int playedInCurrentCycle;
}
