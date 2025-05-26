package com.worklyze.worklyze.domain.enums;

import lombok.Getter;

@Getter
public enum TypeProviderEnum {
    GOOGLE(1L),
    LOCAL(2L);

    private Long value;

    TypeProviderEnum(Long value) {
        this.value = value;
    }
}
