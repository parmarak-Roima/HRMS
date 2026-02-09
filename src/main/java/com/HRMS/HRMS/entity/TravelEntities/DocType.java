package com.HRMS.HRMS.entity.TravelEntities;

import com.HRMS.HRMS.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "doc_type")
@Data
public class DocType extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_name")
    private String typeName;
}
