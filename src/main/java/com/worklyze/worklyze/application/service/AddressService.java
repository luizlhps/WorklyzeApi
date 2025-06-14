package com.worklyze.worklyze.application.service;

import com.worklyze.worklyze.domain.entity.Address;
import com.worklyze.worklyze.infra.repository.AddressRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AddressService extends BaseServiceImpl<Address, UUID> {
    public AddressService(AddressRepositoryImpl repository, ModelMapper modelMapper, EntityManager entityManager) {
        super(repository, modelMapper, entityManager);
    }
}
