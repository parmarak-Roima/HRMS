package com.HRMS.HRMS.entity;

import com.HRMS.HRMS.entity.Enums.Designations;
import com.HRMS.HRMS.entity.Enums.EmployeeRole;
import com.HRMS.HRMS.entity.TravelEntities.ExpenseType;
import com.HRMS.HRMS.entity.TravelEntities.Travel;
import com.HRMS.HRMS.entity.TravelEntities.TravelAssignment;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
@Getter
@Setter
@Data
@ToString(exclude = {"manager", "subordinates"})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Designations designation;

    @Column(nullable = false, unique = true)
    private String email;

    private String profileUrl;

    @Column(nullable = false)
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false)
    private LocalDate joiningDate;

    // Self-referencing relationship: Employee reports to Manager (another Employee)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id") // emp_id(self-reference)
    private Employee manager;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Travel> createdTravels = new ArrayList<>();

    @OneToMany(mappedBy = "employee",cascade = CascadeType.ALL , orphanRemoval = true)
    private List<TravelAssignment> travelAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "manager")
    private List<Employee> subordinates;

    public void addSubordinate(Employee subordinate) {
        subordinates.add(subordinate);
        subordinate.setManager(this);
    }

    public void removeSubordinate(Employee subordinate) {
        subordinates.remove(subordinate);
        subordinate.setManager(null);
    }

    // For createdTravels
    public void addCreatedTravel(Travel travel) {
        createdTravels.add(travel);
        travel.setCreatedBy(this); // Set the owning side
    }

    public void removeCreatedTravel(Travel travel) {
        createdTravels.remove(travel);
        travel.setCreatedBy(null); // Break the link
    }

    // For travelAssignments
    public void addTravelAssignment(TravelAssignment assignment) {
        travelAssignments.add(assignment);
        assignment.setEmployee(this); // Set the owning side
    }

    public void removeTravelAssignment(TravelAssignment assignment) {
        travelAssignments.remove(assignment);
        assignment.setEmployee(null); // Break the link
    }

}
