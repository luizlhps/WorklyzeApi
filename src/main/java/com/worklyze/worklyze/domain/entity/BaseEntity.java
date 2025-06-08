package com.worklyze.worklyze.domain.entity;

import com.worklyze.worklyze.domain.interfaces.entity.Identifiable;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity<ID> implements Identifiable<ID> {
    @Column(updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
