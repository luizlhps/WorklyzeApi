package com.worklyze.worklyze.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Demand extends BaseEntity<UUID> {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String totalTime;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private TypeStatus typeStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}