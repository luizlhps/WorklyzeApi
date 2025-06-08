package com.worklyze.worklyze.domain.interfaces.repository;

import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import com.worklyze.worklyze.shared.page.interfaces.QueryParams;

import java.lang.reflect.InvocationTargetException;

public interface BaseRepository<TEntity, TId> {
    <TOutDto, TInputDto extends QueryParams> PageResult<TOutDto> findAll(TInputDto dtoClass, Class<TOutDto> dtoOutClass);

    TEntity create(TEntity entity);

    TEntity update(TEntity entity) ;

    TEntity findById(TId id);
}
