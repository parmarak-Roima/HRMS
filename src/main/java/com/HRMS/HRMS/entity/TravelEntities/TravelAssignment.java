package com.HRMS.HRMS.entity.TravelEntities;
import com.HRMS.HRMS.entity.BaseEntity;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Enums.TravelStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "travel_assignment")
@Data
public class TravelAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "s_date")
    private LocalDate startDate;

    @Column(name = "e_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TravelStatus status;

    @OneToMany(mappedBy = "travelAssignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelExpense> travelExpenses = new ArrayList<>();

    public void addTravelExpense(TravelExpense expense) {
        travelExpenses.add(expense);
        expense.setTravelAssignment(this);
    }

    public void removeTravelExpense(TravelExpense expense) {
        travelExpenses.remove(expense);
        expense.setTravelAssignment(null);
    }

}
