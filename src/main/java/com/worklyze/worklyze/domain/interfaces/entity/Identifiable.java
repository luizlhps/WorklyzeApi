package com.worklyze.worklyze.domain.interfaces.entity;


public interface Identifiable<ID> {
    ID getId();
    Boolean getDeleted();
    void setDeleted(Boolean deleted);
}