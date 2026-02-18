package com.HRMS.HRMS.entity.GameEntities;

import com.HRMS.HRMS.entity.BaseEntity;
import com.HRMS.HRMS.entity.Employee;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "booking_request")
@Data
public class BookingRequest extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private GameSlot slot;

    @ManyToOne
    @JoinColumn(name = "primary_booker_id")
    private Employee primaryBooker;

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // PENDING, CONFIRMED, REJECTED, CANCELLED

    private LocalDateTime requestedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "bookingRequest", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<BookingParticipant> participants;

    public enum RequestStatus {
        PENDING, CONFIRMED, REJECTED, CANCELLED
    }
}
