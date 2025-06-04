package com.worklyze.worklyze.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseEntity<UUID> {

    @Id
    @GeneratedValue
    private UUID id;

    private String nome;
    private String tempoTotal;
    private String restTotal;

    @ManyToOne
    @JoinColumn(name = "demand_id")
    private Demand demand;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private TypeStatus typeStatus;
}