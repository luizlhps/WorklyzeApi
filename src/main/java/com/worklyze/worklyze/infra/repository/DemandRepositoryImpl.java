package com.worklyze.worklyze.infra.repository;

import com.worklyze.worklyze.domain.entity.Demand;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class DemandRepositoryImpl extends BaseRepositoryImpl<Demand, UUID> {
    public DemandRepositoryImpl(EntityManager em, ModelMapper modelMapper) {
        super(em, modelMapper, Demand.class);
    }
}
