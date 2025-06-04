package com.worklyze.worklyze.domain.interfaces;

import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import com.worklyze.worklyze.shared.page.interfaces.QueryParams;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface BaseRepository<TEntity, TId> {
    <TOutDto, TInputDto extends QueryParams> PageResult<TOutDto> findAll(TInputDto dtoClass, Class<TOutDto> dtoOutClass);

    TEntity create(TEntity entity);

    TEntity update(TEntity entity) throws InvocationTargetException, IllegalAccessException;

    TEntity findById(TId id);
}
