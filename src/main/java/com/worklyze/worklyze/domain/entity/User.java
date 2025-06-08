package com.worklyze.worklyze.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="\"user\"")
public class User extends BaseEntity<UUID> {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
    private String recoveryToken;
    private OffsetDateTime recoveryExpiryTime;
    private String refreshToken;
    private OffsetDateTime refreshTokenExpiryTime;

    @OneToMany(mappedBy = "user")
    private List<Address> addresses;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private TypeProvider typeProvider;
}