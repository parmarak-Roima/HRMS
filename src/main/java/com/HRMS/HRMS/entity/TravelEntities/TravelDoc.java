package com.HRMS.HRMS.entity.TravelEntities;

import com.HRMS.HRMS.entity.BaseEntity;
import com.HRMS.HRMS.entity.Employee;
import com.HRMS.HRMS.entity.Enums.DocType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "travel_docs")
@Data
public class TravelDoc extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "travel_id")
    private Travel travel;

    @Enumerated(EnumType.STRING)
    private DocType docType;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    private Employee uploadedBy;

    // Nullable: If null, it's a shared doc. If set, it's private to that user.
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Employee owner;

    @Column(name = "file_url")
    private String fileUrl;
}
