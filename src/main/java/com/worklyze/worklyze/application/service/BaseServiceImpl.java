package com.worklyze.worklyze.application.service;

import com.worklyze.worklyze.domain.interfaces.entity.Identifiable;
import com.worklyze.worklyze.domain.interfaces.repository.BaseRepository;
import com.worklyze.worklyze.domain.interfaces.services.BaseService;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import com.worklyze.worklyze.shared.page.interfaces.QueryParams;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public abstract class BaseServiceImpl<TEntity extends Identifiable<TId>, TId> implements BaseService<TEntity, TId> {
    protected final BaseRepository<TEntity, TId> repository;
    protected final ModelMapper modelMapper;
    private final EntityManager entityManager;

    public BaseServiceImpl(BaseRepository<TEntity, TId> repository, ModelMapper modelMapper, EntityManager entityManager) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.entityManager = entityManager;
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
    public <TOutDto, TInputDto> TOutDto update(TInputDto dtoClass, Class<TEntity> entityClass, Class<TOutDto> dtoOutClass) {
        TEntity tEntity = modelMapper.map(dtoClass, entityClass);
        TId id = tEntity.getId();

        TEntity entityFound = findById(id, entityClass);
        normalizeEntityReferences(dtoClass, entityFound);

        modelMapper.map(dtoClass, entityFound);

        TEntity entityUpdated = repository.update(entityFound);

        return modelMapper.map(entityUpdated, dtoOutClass);
    }

    @Override
    public <TOutDto> TOutDto findById(TId id, Class<TOutDto> dtoOutClass) {
        TEntity tEntity = repository.findById(id);
        return modelMapper.map(tEntity, dtoOutClass);
    }

    @Override
    public void delete(TId id) {
        TEntity tEntity = repository.findById(id);

        tEntity.setDeleted(true);

        repository.update(tEntity);
    }

    private void normalizeEntityReferences(Object dto, Object entityTarget) {
        Field[] fields = dto.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object dtoNested = field.get(dto);
                if (dtoNested == null) continue;

                AutoMap autoMap = dtoNested.getClass().getAnnotation(AutoMap.class);
                if (autoMap == null) continue;

                Field idField = Arrays.stream(dtoNested.getClass().getDeclaredFields())
                        .filter(f -> f.getName().equals("id"))
                        .findFirst()
                        .orElse(null);

                if (idField == null) continue;

                idField.setAccessible(true);
                Object idValue = idField.get(dtoNested);
                if (idValue == null) continue;

                Object reference = entityManager.getReference(autoMap.value(), idValue);

                Field entityField = entityTarget.getClass().getDeclaredField(field.getName());
                entityField.setAccessible(true);
                entityField.set(entityTarget, reference);

            } catch (Exception e) {
                throw new RuntimeException("Failed to normalize entity references", e);
            }
        }
    }
}