package com.worklyze.worklyze.infra.repository;

import com.worklyze.worklyze.domain.interfaces.repository.BaseRepository;
import com.worklyze.worklyze.domain.interfaces.entity.Identifiable;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import com.worklyze.worklyze.shared.exceptions.NotFoundException;
import com.worklyze.worklyze.shared.page.PageResultImpl;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import com.worklyze.worklyze.shared.page.interfaces.QueryParams;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

@RequiredArgsConstructor
public class BaseRepositoryImpl<TEntity extends Identifiable<TId>, TId> implements BaseRepository<TEntity, TId> {

    protected final EntityManager em;
    protected final ModelMapper modelMapper;
    protected final Class<TEntity> entityClass;

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
        buildSelectionsFromDtoFields(dtoOutClass, "", root, selections, joins);
        cq.multiselect(selections);

        // Filtros com base nos campos preenchidos do DTO de entrada
        buildPredicates(dtoIn, root, joins, predicates, cb, cq);

        TypedQuery<Tuple> tuples = em.createQuery(cq);

        if (!dtoIn.getPagination().getAllRows()) {
            int offset = dtoIn.getPagination().getOffset();
            int size = dtoIn.getPagination().getPageSize();

            tuples.setFirstResult(offset);
            tuples.setMaxResults(size);
        }

        var result = tuples.getResultList();

        Map<Object, Map<String, List<Object>>> groupedCollectionValues = new HashMap<>();

        Map<Object, Tuple> primaryTuples = new HashMap<>();


        /*Sem esse agrupamento, acabaria criando um DTO por linha no resultado,
        repetindo o pai N vezes em vez de ter N filhos dentro de um único pai.
        Essa é a razão de existir desse laço “duplo” que separa as tuplas de pai
        e coleta os valores das coleções.*/

        for (Tuple tuple : result) {
            Object primaryKey = tuple.get("id");
            // guarda a primeira ocorrência do Tuple de cada entidade-pai
            primaryTuples.putIfAbsent(primaryKey, tuple);

            for (Selection<?> selection : selections) {
                String alias = selection.getAlias();

                if (alias.contains(".")) {
                    String[] parts = alias.split("\\.");
                    String collectionPath = parts[0];
                    Object value = tuple.get(alias);

                    if (value == null) continue;

                    // vai acumulando os valores daquela coleção, por id de pai
                    groupedCollectionValues
                            .computeIfAbsent(primaryKey, k -> new HashMap<>())
                            .computeIfAbsent(collectionPath, k -> new ArrayList<>())
                            .add(value);

                }
            }
        }

        var totalCount = totalCount(cb, entityClass);

        PageResultImpl<TOutDto> pageResult = buildPageResult(dtoIn, totalCount);

        // Map Tuples to DTOs
        List<TOutDto> items = primaryTuples.values().stream()
                .map(tuple -> {
                    try {
                        TOutDto dto = dtoOutClass.getDeclaredConstructor().newInstance();
                        Object primaryKey = tuple.get("id");
                        Map<String, List<Object>> collectionValues = groupedCollectionValues.getOrDefault(primaryKey, Collections.emptyMap());
                        buildOutDto(dtoOutClass, "", tuple, dto, collectionValues);
                        return dto;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException("Erro ao mapear Tuple para DTO", e);
                    }
                })
                .toList();

        pageResult.setItems(items);
        return pageResult;
    }

    private <TInputDto extends QueryParams> void buildPredicates(TInputDto dtoIn, Root<?> root, Map<String, Join<?, ?>> joins, List<Predicate> predicates, CriteriaBuilder cb, CriteriaQuery<Tuple> cq) {
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
    }


    private Long totalCount(CriteriaBuilder cb, Class<?> entityClass) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<?> countRoot = countQuery.from(entityClass);
        countQuery.select(cb.count(countRoot));

        var totalCount = em.createQuery(countQuery).getSingleResult();
        return totalCount;
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

    private void buildSelectionsFromDtoFields(
            Class<?> dtoClass,
            String prefix,
            From<?, ?> rootOrJoin,
            List<Selection<?>> selections,
            Map<String, Join<?, ?>> joins
    ) {
        for (Field field : dtoClass.getDeclaredFields()) {
            field.setAccessible(true);

            String fieldPath = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();

            if (Collection.class.isAssignableFrom(field.getType())) {
                handleCollectionJoin((From<?, ?>) rootOrJoin, selections, joins, field, fieldPath);
                continue;
            }

            //responsavel por tipos primitivos
            if (field.getType().getName().startsWith("java.") || field.getType().isPrimitive()) {
                Path<?> path = resolvePath(rootOrJoin, fieldPath, joins);
                selections.add(path.alias(fieldPath));
                continue;
            }


            handleNestedClassJoin((From<?, ?>) rootOrJoin, selections, joins, field, fieldPath);
        }
    }

    private void handleNestedClassJoin(From<?, ?> rootOrJoin, List<Selection<?>> selections, Map<String, Join<?, ?>> joins, Field field, String fieldPath) {
        AutoMap nestedAutoMap = field.getType().getAnnotation(AutoMap.class);
        if (nestedAutoMap != null) {
            final From<?, ?> finalRootOrJoin = rootOrJoin;
            Join<?, ?> join = joins.computeIfAbsent(field.getName(), k -> finalRootOrJoin.join(k, JoinType.LEFT));
            buildSelectionsFromDtoFields(field.getType(), fieldPath, join, selections, joins);
        }
    }


    private void handleCollectionJoin(From<?, ?> rootOrJoin, List<Selection<?>> selections, Map<String, Join<?, ?>> joins, Field field, String fieldPath) {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        Class<?> genericType = (Class<?>) listType.getActualTypeArguments()[0];

        AutoMap nestedAutoMap = genericType.getAnnotation(AutoMap.class);

        if (nestedAutoMap != null) {
            Join<?, ?> join = joins.computeIfAbsent(field.getName(), k -> rootOrJoin.join(k, JoinType.LEFT));

            buildSelectionsFromDtoFields(genericType, fieldPath, join, selections, joins);
        }
    }

    private Path<?> resolvePath(
            From<?, ?> rootOrJoin,
            String pathStr,
            Map<String, Join<?, ?>> joins
    ) {
        String[] parts = pathStr.split("\\.");
        Path<?> path = rootOrJoin;
        String currentPrefix = "";

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            currentPrefix = currentPrefix.isEmpty() ? part : currentPrefix + "." + part;

            if (joins.containsKey(currentPrefix)) {
                path = joins.get(currentPrefix);
            } else {
                if (i < parts.length - 1) {
                    // nível intermediário
                    Join<?, ?> join = ((From<?, ?>) path).join(part, JoinType.LEFT);
                    joins.put(currentPrefix, join);
                    path = join;
                } else {
                    // ultimo elemento
                    path = path.get(part);
                }
            }
        }

        return path;
    }


    private static <TDto> void buildOutDto(Class<TDto> dtoClass, String prefix, Tuple tuple, TDto dto, Map<String, List<Object>> collectionValues) {
        try {
            for (Field field : dtoClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldPath = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();

                if (Collection.class.isAssignableFrom(field.getType())) {
                    handleCollectionOutDto(tuple, dto, collectionValues, field, fieldPath);
                    continue;
                }

                if (field.getType().getName().startsWith("java.") || field.getType().isPrimitive()) {
                    Object value = tuple.get(fieldPath);
                    field.set(dto, value);

                    continue;
                }

                if (!Collection.class.isAssignableFrom(field.getType())
                        && !field.getType().getName().startsWith("java.")
                        && !field.getType().isPrimitive()) {

                    if (isAllNullForPrefix(tuple, fieldPath)) {
                        continue;
                    }
                }

                Object nestedInstance = field.getType().getDeclaredConstructor().newInstance();

                @SuppressWarnings("unchecked")
                Class<Object> safeFieldType = (Class<Object>) field.getType();
                buildOutDto(safeFieldType, fieldPath, tuple, nestedInstance, collectionValues);
                field.set(dto, nestedInstance);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Erro ao mapear Tuple para DTO", e);
        }
    }

    private static boolean isAllNullForPrefix(Tuple tuple, String prefix) {
        String prefixDot = prefix + ".";
        return tuple.getElements().stream()
                .map(TupleElement::getAlias)
                .filter(alias -> alias.startsWith(prefixDot))
                .allMatch(alias -> tuple.get(alias) == null);
    }

    private static <TDto> void handleCollectionOutDto(Tuple tuple, TDto dto, Map<String, List<Object>> collectionValues, Field field, String fieldPath) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ParameterizedType collectionType = (ParameterizedType) field.getGenericType();
        Class<?> elementType = (Class<?>) collectionType.getActualTypeArguments()[0];

        @SuppressWarnings("unchecked")
        Collection<Object> collectionInstance = (Collection<Object>) field.getType().getDeclaredConstructor().newInstance();

        List<Object> values = collectionValues.getOrDefault(fieldPath, Collections.emptyList());

        for (Object value : values) {
            if (elementType.getName().startsWith("java.") || elementType.isPrimitive()) {
                collectionInstance.add(value);
                continue;
            }
            Object elementInstance = ((Class<Object>) elementType).getDeclaredConstructor().newInstance();

            @SuppressWarnings("unchecked")
            Class<Object> safeElementType = (Class<Object>) elementType;
            buildOutDto(safeElementType, fieldPath, tuple, elementInstance, collectionValues);
            collectionInstance.add(elementInstance);
        }

        field.set(dto, collectionInstance);
    }

    private <TOutDto, TInputDto extends QueryParams> PageResultImpl<TOutDto> buildPageResult(TInputDto dtoIn, Long totalCount) {
        int pageNumber = dtoIn.getPagination().getPageNumber();
        int pageSize = dtoIn.getPagination().getPageSize();

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        var pageResult = new PageResultImpl<TOutDto>();
        pageResult.setPageNumber(pageNumber);
        pageResult.setPageSize(pageSize);
        pageResult.setTotalCount(totalCount.intValue());
        pageResult.setTotalPages(totalPages);
        return pageResult;
    }
}