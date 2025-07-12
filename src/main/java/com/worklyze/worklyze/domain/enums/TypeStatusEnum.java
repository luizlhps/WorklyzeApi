package com.worklyze.worklyze.domain.enums;

import com.worklyze.worklyze.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public enum TypeStatusEnum {
    ABERTO(1L),
    FECHADO(2L),
    DENSEVOLVIMENTO(3L),
    TESTES(4L);

    private final Long value;

    TypeStatusEnum(Long value) {
        this.value = value;
    }
}

