package com.worklyze.worklyze.domain.interfaces.services;

import com.worklyze.worklyze.application.dto.demand.DemandCreateInDto;
import com.worklyze.worklyze.application.dto.demand.DemandCreateOutDto;
import com.worklyze.worklyze.domain.entity.Demand;

import java.util.UUID;

public interface DemandService extends BaseService<Demand, UUID> {
    DemandCreateOutDto create(DemandCreateInDto dto);

}
