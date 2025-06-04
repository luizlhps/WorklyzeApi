package com.worklyze.worklyze.infra.repository;

import com.worklyze.worklyze.domain.entity.Address;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class AddressRepositoryImpl extends BaseRepositoryImpl<Address, UUID> {
    public AddressRepositoryImpl(EntityManager em, ModelMapper modelMapper) {
        super(em, modelMapper, Address.class);
    }
}
