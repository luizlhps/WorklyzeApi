package com.worklyze.worklyze.application.service;

import com.worklyze.worklyze.domain.entity.TypeStatus;
import com.worklyze.worklyze.domain.interfaces.services.TypeStatusService;
import com.worklyze.worklyze.infra.repository.TypeStatusRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class TypeStatusServiceImpl extends BaseServiceImpl<TypeStatus, Long> implements TypeStatusService {
    public TypeStatusServiceImpl(TypeStatusRepositoryImpl repository, ModelMapper modelMapper, EntityManager entityManager) {
        super(repository, modelMapper, entityManager);
    }

}
