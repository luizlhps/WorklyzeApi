package com.worklyze.worklyze.application.service;

import com.worklyze.worklyze.application.dto.AddressGetAllInDto;
import com.worklyze.worklyze.application.dto.AddressGetAllOutDto;
import com.worklyze.worklyze.domain.entity.Address;
import com.worklyze.worklyze.domain.interfaces.repository.BaseRepository;
import com.worklyze.worklyze.infra.repository.AddressRepositoryImpl;
import com.worklyze.worklyze.infra.repository.BaseRepositoryImpl;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService extends BaseServiceImpl<Address, UUID> {
    public AddressService(AddressRepositoryImpl repository, ModelMapper modelMapper) {
        super(repository, modelMapper);
    }

    // private final BaseRepositoryImpl<Address, UUID> addressRepository;

/*    public AddressService(BaseRepositoryImpl<Address, UUID> addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Transactional
    public Address create(Address address) {
        return addressRepository.create(address);
    }


    //todo arrumar
    @Transactional
    public Address update(Address address) {
        UUID id = address.getId();
        Address existing = addressRepository.findById(id);

        return addressRepository.update(address);
    }

    public Address findById(UUID id) {
        return addressRepository.findById(id);
    }

    public PageResult<AddressGetAllOutDto> findAll(AddressGetAllInDto dto) {
        return addressRepository.findAll(dto, AddressGetAllOutDto.class);
    }*/
}
