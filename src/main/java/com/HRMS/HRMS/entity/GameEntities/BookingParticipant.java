package com.HRMS.HRMS.entity.GameEntities;

import com.HRMS.HRMS.entity.BaseEntity;
import com.HRMS.HRMS.entity.Employee;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "br_participation")
@Data
public class BookingParticipant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "br_id")
    private BookingRequest bookingRequest;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    private Employee employee;
}
