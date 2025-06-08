package com.worklyze.worklyze.application.service;

import com.worklyze.worklyze.domain.interfaces.entity.Identifiable;
import com.worklyze.worklyze.domain.interfaces.repository.BaseRepository;
import com.worklyze.worklyze.domain.interfaces.services.BaseService;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import com.worklyze.worklyze.shared.page.interfaces.QueryParams;
import org.modelmapper.ModelMapper;

import java.lang.reflect.InvocationTargetException;

public class BaseServiceImpl<TEntity extends Identifiable<TId>, TId> implements BaseService<TEntity, TId> {
    private final BaseRepository<TEntity, TId> repository;
    private final ModelMapper modelMapper;

    public BaseServiceImpl(BaseRepository<TEntity, TId> repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public <TOutDto, TInputDto extends QueryParams> PageResult<TOutDto> findAll(TInputDto dtoClass, Class<TOutDto> dtoOutClass) {
        return repository.findAll(dtoClass, dtoOutClass);
    }

    @Override
    public <TOutDto, TInputDto> TOutDto create(TInputDto dtoClass, Class<TEntity> entityClass, Class<TOutDto> dtoOutClass) {
        TEntity tEntity = modelMapper.map(dtoClass, entityClass);

        TEntity tEntityCreated = repository.create(tEntity);

        return modelMapper.map(tEntityCreated, dtoOutClass);
    }

    @Override
    public <TOutDto, TInputDto> TOutDto update(TInputDto dtoClass, Class<TEntity> entityClass, Class<TOutDto> dtoOutClass)  {
        TEntity tEntity = modelMapper.map(dtoClass, entityClass);

        TId id = tEntity.getId();
        TEntity entityFound = findById(id, entityClass);

        modelMapper.map(tEntity, entityFound);

        TEntity entityUpdated = repository.update(tEntity);

        return modelMapper.map(entityUpdated, dtoOutClass);
    }

    @Override
    public <TOutDto> TOutDto findById(TId id, Class<TOutDto> dtoOutClass) {
        TEntity tEntity =  repository.findById(id);
        return modelMapper.map(tEntity, dtoOutClass);
    }
}