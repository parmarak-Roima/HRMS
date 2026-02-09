package com.HRMS.HRMS.entity.TravelEntities;

import com.HRMS.HRMS.entity.BaseEntity;
import com.HRMS.HRMS.entity.Enums.ExpenseStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "travel_expense")
@Data
public class TravelExpense extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "travel_assignment_id", nullable = false)
    private TravelAssignment travelAssignment;

    @Column(name = "proof_url")
    private String proofUrl;

    @Enumerated(EnumType.STRING)
    private ExpenseStatus status;

    @Column(columnDefinition = "TEXT",nullable = true)
    private String remarks;

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "expense_type_id")
    private ExpenseType expenseType;

    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String description;
}

