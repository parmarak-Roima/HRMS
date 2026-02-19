package com.HRMS.HRMS.entity.GameEntities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "game_slots")
@Data
@NoArgsConstructor
public class GameSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "game_id",nullable = false)
    private Game game;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private LocalTime startTime;
    @Column(nullable = false)
    private LocalTime endTime;
    @Enumerated(EnumType.STRING)
    private SlotStatus status;

    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<BookingRequest> bookingRequests;

    public GameSlot(Game game, LocalDate localDate, LocalTime slotStart, LocalTime slotEnd, SlotStatus slotStatus) {
        this.game = game;
        this.date = localDate;
        this.startTime = slotStart;
        this.endTime = slotEnd;
        this.status = slotStatus;
    }

    public enum SlotStatus {
        OPEN, LOCKED, BOOKED
    }
}
