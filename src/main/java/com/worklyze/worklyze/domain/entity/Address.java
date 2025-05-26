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
public class Address extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String city;
    private String street;
    private String state;
    private String zip;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}