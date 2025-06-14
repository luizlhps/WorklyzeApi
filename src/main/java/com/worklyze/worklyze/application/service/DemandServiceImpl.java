package com.worklyze.worklyze.application.service;

import com.worklyze.worklyze.application.dto.demand.DemandCreateInDto;
import com.worklyze.worklyze.application.dto.demand.DemandCreateOutDto;
import com.worklyze.worklyze.domain.entity.Demand;
import com.worklyze.worklyze.domain.entity.TypeStatus;
import com.worklyze.worklyze.domain.enums.TypeStatusEnum;
import com.worklyze.worklyze.domain.interfaces.repository.BaseRepository;
import com.worklyze.worklyze.domain.interfaces.services.DemandService;
import com.worklyze.worklyze.infra.repository.DemandRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class DemandServiceImpl extends BaseServiceImpl<Demand, UUID> implements DemandService  {
    public DemandServiceImpl(DemandRepositoryImpl repository, ModelMapper modelMapper, EntityManager entityManager) {
        super(repository, modelMapper, entityManager);
    }

    @Override
    public DemandCreateOutDto create(DemandCreateInDto dto) {
        var typeStatus = new DemandCreateInDto.TypeStatusDto();
        typeStatus.setId(TypeStatusEnum.ABERTO.getValue());
        dto.setTypeStatus(typeStatus);

        var entity = modelMapper.map(dto, Demand.class);
        entity.setTotalTime(Duration.ZERO);

        Demand tEntityCreated = repository.create(entity);

        return modelMapper.map(tEntityCreated, DemandCreateOutDto.class);
    }
}
