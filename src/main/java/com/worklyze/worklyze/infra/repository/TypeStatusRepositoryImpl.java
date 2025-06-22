package com.worklyze.worklyze.infra.repository;

import com.worklyze.worklyze.domain.entity.Address;
import com.worklyze.worklyze.domain.entity.TypeStatus;
import com.worklyze.worklyze.domain.interfaces.repository.TypeStatusRepository;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class TypeStatusRepositoryImpl extends BaseRepositoryImpl<TypeStatus, Long> implements TypeStatusRepository {
    public TypeStatusRepositoryImpl(EntityManager em, ModelMapper modelMapper) {
        super(em, modelMapper, TypeStatus.class);
    }
}
