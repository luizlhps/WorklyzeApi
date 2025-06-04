package com.worklyze.worklyze.infra.repository;

import com.worklyze.worklyze.domain.interfaces.BaseRepository;
import com.worklyze.worklyze.domain.interfaces.Identifiable;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import com.worklyze.worklyze.shared.exceptions.BadRequestException;
import com.worklyze.worklyze.shared.exceptions.NotFoundException;
import com.worklyze.worklyze.shared.page.PageResultImpl;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import com.worklyze.worklyze.shared.page.interfaces.QueryParams;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

@RequiredArgsConstructor
public class BaseRepositoryImpl<TEntity extends Identifiable<TId>, TId> implements BaseRepository<TEntity, TId> {

    private final EntityManager em;
    private final ModelMapper modelMapper;
    private final Class<TEntity> entityClass;

    @Override
    public <TOutDto, TInputDto extends QueryParams> PageResult<TOutDto> findAll(TInputDto dtoIn, Class<TOutDto> dtoOutClass) {
        AutoMap autoMap = dtoIn.getClass().getAnnotation(AutoMap.class);
        if (autoMap == null) {
            throw new IllegalArgumentException("DTO de entrada precisa ter @AutoMap apontando para a entidade.");
        }

        Class<?> entityClass = autoMap.value();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<?> root = cq.from(entityClass);

        Map<String, Join<?, ?>> joins = new HashMap<>();
        List<Selection<?>> selections = new ArrayList<>();
        List<Predicate> predicates = new ArrayList<>();

        // Projeta os campos do DTO de saída
        processDtoFields(dtoOutClass, "", root, selections, joins);
        cq.multiselect(selections);

        // Filtros com base nos campos preenchidos do DTO de entrada
        if (dtoIn != null) {
            for (Field field : dtoIn.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(dtoIn);
                    if (value != null) {
                        Path<?> path = resolvePath(root, field.getName(), joins);
                        predicates.add(cb.equal(path, value));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Erro ao acessar campo do DTO de entrada", e);
                }
            }
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        TypedQuery<Tuple> tuples = em.createQuery(cq);

        if (!dtoIn.getPagination().getAllRows()) {
            int offset = dtoIn.getPagination().getOffset();
            int size = dtoIn.getPagination().getPageSize();

            tuples.setFirstResult(offset);
            tuples.setMaxResults(size);
        }

        var result = tuples.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<?> countRoot = countQuery.from(entityClass);
        countQuery.select(cb.count(countRoot));

        var totalCount = em.createQuery(countQuery).getSingleResult();

        int pageNumber = dtoIn.getPagination().getPageNumber();
        int pageSize = dtoIn.getPagination().getPageSize();

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        var pageResult = new PageResultImpl<TOutDto>();
        pageResult.setPageNumber(pageNumber);
        pageResult.setPageSize(pageSize);
        pageResult.setTotalCount(totalCount.intValue());
        pageResult.setTotalPages(totalPages);

        var items = result.stream()
                .map(tuple -> mapTupleToDto(dtoOutClass, tuple))
                .toList();

        pageResult.setItems(items);

        return pageResult;
    }

    @Transactional
    @Override
    public TEntity create(TEntity entity) {
        em.persist(entity);
        return entity;
    }

    @Transactional
    @Override
    public TEntity update(TEntity entity) {
        return em.merge(entity);
    }

    @Override
    public TEntity findById(TId id) {
        TEntity existingEntity = em.find(entityClass, id);

        if (existingEntity == null) {
            throw new NotFoundException("Entidade não encontrada", "BASE_REPO_ENTITY_NOT_FOUND");
        }

        return existingEntity;
    }

    private void processDtoFields(Class<?> dtoClass, String prefix, From<?, ?> rootOrJoin,
                                  List<Selection<?>> selections, Map<String, Join<?, ?>> joins) {
        for (Field field : dtoClass.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldPath = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();

            if (Collection.class.isAssignableFrom(field.getType())) {
                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                Class<?> genericType = (Class<?>) listType.getActualTypeArguments()[0];

                AutoMap nestedAutoMap = genericType.getAnnotation(AutoMap.class);
                if (nestedAutoMap != null) {
                    Join<?, ?> join = joins.computeIfAbsent(field.getName(),
                            k -> ((From<?, ?>) rootOrJoin).join(k, JoinType.LEFT));
                    processDtoFields(genericType, fieldPath, join, selections, joins);
                }
            } else if (field.getType().getName().startsWith("java.") || field.getType().isPrimitive()) {
                Path<?> path = resolvePath(rootOrJoin, fieldPath, joins);
                selections.add(path.alias(fieldPath));
            } else {
                AutoMap nestedAutoMap = field.getType().getAnnotation(AutoMap.class);
                if (nestedAutoMap != null) {
                    Join<?, ?> join = joins.computeIfAbsent(field.getName(),
                            k -> ((From<?, ?>) rootOrJoin).join(k, JoinType.LEFT));
                    processDtoFields(field.getType(), fieldPath, join, selections, joins);
                }
            }
        }
    }

    private Path<?> resolvePath(From<?, ?> rootOrJoin, String pathStr, Map<String, Join<?, ?>> joins) {
        String[] parts = pathStr.split("\\.");
        Path<?> path = rootOrJoin;
        for (String part : parts) {
            if (joins.containsKey(part)) {
                path = joins.get(part);
            } else {
                path = path.get(part);
            }
        }
        return path;
    }

    private <TDto> TDto mapTupleToDto(Class<TDto> dtoClass, Tuple tuple) {
        try {
            TDto dto = dtoClass.getDeclaredConstructor().newInstance();

            for (Field field : dtoClass.getDeclaredFields()) {
                field.setAccessible(true);
                String alias = field.getName();

                if (Collection.class.isAssignableFrom(field.getType())) continue;

                if (field.getType().getName().startsWith("java.") || field.getType().isPrimitive()) {
                    Object value = tuple.get(alias);
                    field.set(dto, value);
                } else {
                    Object nestedInstance = field.getType().getDeclaredConstructor().newInstance();
                    for (Field nestedField : field.getType().getDeclaredFields()) {
                        nestedField.setAccessible(true);
                        String nestedAlias = alias + "." + nestedField.getName();
                        Object nestedValue = tuple.get(nestedAlias);
                        nestedField.set(nestedInstance, nestedValue);
                    }
                    field.set(dto, nestedInstance);
                }
            }

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao mapear Tuple para DTO", e);
        }
    }
}