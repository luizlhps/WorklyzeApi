package com.worklyze.worklyze.domain.interfaces.services;

import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import com.worklyze.worklyze.shared.page.interfaces.QueryParams;

import java.lang.reflect.InvocationTargetException;

public interface BaseService<TEntity, TId> {
    <TOutDto, TInputDto extends QueryParams> PageResult<TOutDto> findAll(TInputDto dtoClass, Class<TOutDto> dtoOutClass);

    <TOutDto, TInputDto> TOutDto create(TInputDto dtoClass, Class<TEntity> entityClass, Class<TOutDto> dtoOutClass);

    <TOutDto, TInputDto> TOutDto update(TInputDto dtoClass, Class<TEntity> entityClass, Class<TOutDto> dtoOutClass) throws InvocationTargetException, IllegalAccessException;

    <TOutDto> TOutDto findById(TId id, Class<TOutDto> dtoOutClass);
}