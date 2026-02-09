package com.HRMS.HRMS.entity.TravelEntities;

import com.HRMS.HRMS.entity.BaseEntity;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Enums.TravelStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "travel")
@Data
public class Travel  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private Employee createdBy;

    @Column
    private String destination;

    @Column(name = "s_date")
    private LocalDate startDate;

    @Column(name = "e_date")
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TravelStatus status;

    @Column(name = "required_docs", columnDefinition = "TEXT")
    private String requiredDocs;

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelAssignment> travelAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelDoc> travelDocList = new ArrayList<>();

    public void addTravelAssignment(TravelAssignment assignment) {
        travelAssignments.add(assignment);
        assignment.setTravel(this);
    }

    public void removeTravelAssignment(TravelAssignment assignment) {
        travelAssignments.remove(assignment);
        assignment.setTravel(null);
    }

    public void addTravelDoc(TravelDoc doc) {
        travelDocList.add(doc);
        doc.setTravel(this);
    }

    public void removeTravelDoc(TravelDoc doc) {
        travelDocList.remove(doc);
        doc.setTravel(null);
    }

}
