package com.HRMS.HRMS.entity;

import com.HRMS.HRMS.entity.Enums.Designations;
import com.HRMS.HRMS.entity.Enums.EmployeeRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employees")
@Getter
@Setter
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeRole role;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false)
    private LocalDate joiningDate;

    // Self-referencing relationship: Employee reports to Manager (another Employee)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id") // emp_id(self-reference)
    private Employee manager;

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

}
