package com.worklyze.worklyze.infra.repository;

import com.worklyze.worklyze.domain.interfaces.repository.BaseRepository;
import com.worklyze.worklyze.domain.interfaces.entity.Identifiable;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import com.worklyze.worklyze.shared.annotation.InAnnotation;
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
import java.util.stream.Collectors;

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

        //  Obter os IDs paginados
        List<TId> paginatedIds = getPaginatedIds(dtoIn, entityClass);

        // Obter o total de registros
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Long totalCount = totalCount(cb, entityClass, dtoIn);

        // Construir o PageResult
        PageResultImpl<TOutDto> pageResult = buildPageResult(dtoIn, totalCount);

        if (paginatedIds.isEmpty()) {
            pageResult.setItems(Collections.emptyList());
            return pageResult;
        }

        // Passo 2: Obter os dados completos
        List<TOutDto> items = getFullData(paginatedIds, dtoOutClass);

        pageResult.setItems(items);
        return pageResult;
    }

    private void buildOutDtoSafe(Class<?> clazz, String prefix, Tuple tuple, Object instance, Map<String, List<Object>> collectionValues) {
        buildOutDto(clazz, prefix, tuple, instance, collectionValues);
    }

    private void buildPredicatesRecursively(
            Object dto,
            String prefix,
            From<?, ?> rootOrJoin,
            Map<String, Join<?, ?>> joins,
            List<Predicate> predicates,
            CriteriaBuilder cb
    ) {
        if (dto == null) return;

        Class<?> dtoClass = dto.getClass();
        for (Field field : dtoClass.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(dto);
                if (value != null) {
                    String fieldPath = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();

                    if (field.isAnnotationPresent(InAnnotation.class) && value instanceof Collection<?>) {
                        Collection<?> collection = (Collection<?>) value;
                        if (!collection.isEmpty()) {
                            Path<?> path = resolvePath(rootOrJoin, fieldPath, joins);
                            predicates.add(path.in(collection));
                        }

                        continue;
                    }

                    if (field.getType().getAnnotation(AutoMap.class) != null) {
                        // Campo aninhado
                        Join<?, ?> join = joins.computeIfAbsent(field.getName(), k -> rootOrJoin.join(field.getName(), JoinType.INNER));
                        buildPredicatesRecursively(value, field.getName(), join, joins, predicates, cb);
                    } else {
                        // Campo primitivo
                        Path<?> path = resolvePath(rootOrJoin, fieldPath, joins);
                        predicates.add(cb.equal(path, value));
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Erro ao acessar campo do DTO de entrada", e);
            }
        }
    }

    private <TInputDto extends QueryParams> void buildPredicates(
            TInputDto dtoIn,
            Root<?> root,
            Map<String, Join<?, ?>> joins,
            List<Predicate> predicates,
            CriteriaBuilder cb,
            CriteriaQuery<?> cq
    ) {
        if (dtoIn == null) {
            return;
        }

        // Garantir que o map de joins não seja nulo
        if (joins == null) {
            joins = new HashMap<>();
        }

        buildPredicatesRecursively(dtoIn, "", root, joins, predicates, cb);

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }
    }


    private <TInputDto extends QueryParams> Long totalCount(
            CriteriaBuilder cb,
            Class<?> entityClass,
            TInputDto dtoIn
    ) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<?> countRoot = countQuery.from(entityClass);
        countQuery.select(cb.count(countRoot));

        Map<String, Join<?, ?>> joins = new HashMap<>();
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(countRoot.get("deleted"), false));
        buildPredicates(dtoIn, countRoot, joins, predicates, cb, countQuery);

        return em.createQuery(countQuery).getSingleResult();
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

    private <TOutDto> List<TOutDto> getFullData(List<TId> ids, Class<TOutDto> dtoOutClass) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<?> root = cq.from(entityClass);

        Map<String, Join<?, ?>> joins = new HashMap<>();
        List<Selection<?>> selections = new ArrayList<>();

        buildSelectionsFromDtoFields(dtoOutClass, "", root, selections, joins);
        cq.multiselect(selections);

        Path<TId> idPath = root.get("id");
        cq.where(idPath.in(ids));

        TypedQuery<Tuple> query = em.createQuery(cq);
        List<Tuple> result = query.getResultList();

        Map<TId, List<Tuple>> groupedTuples = result.stream()
                .collect(Collectors.groupingBy(tuple -> (TId) tuple.get("id")));

        return ids.stream()
                .map(id -> {
                    List<Tuple> tuplesForId = groupedTuples.get(id);
                    if (tuplesForId == null || tuplesForId.isEmpty()) {
                        return null;
                    }
                    Tuple anyTuple = tuplesForId.get(0);
                    try {
                        TOutDto dto = dtoOutClass.getDeclaredConstructor().newInstance();
                        for (Field field : dtoOutClass.getDeclaredFields()) {
                            field.setAccessible(true);
                            String fieldName = field.getName();

                            if (Collection.class.isAssignableFrom(field.getType())) {
                                ParameterizedType collectionType = (ParameterizedType) field.getGenericType();
                                Class<?> elementType = (Class<?>) collectionType.getActualTypeArguments()[0];
                                List<Object> collectionItems = tuplesForId.stream()
                                        .filter(tuple -> tuple.get(fieldName + ".id") != null)
                                        .map(tuple -> {
                                            try {
                                                Object elementInstance = elementType.getDeclaredConstructor().newInstance();
                                                buildOutDtoSafe(elementType, fieldName, tuple, elementInstance, Collections.emptyMap());
                                                return elementInstance;
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        })
                                        .collect(Collectors.toList());
                                field.set(dto, collectionItems);
                            } else if (field.getType().getName().startsWith("java.") || field.getType().isPrimitive()) {
                                Object value = anyTuple.get(fieldName);
                                field.set(dto, value);
                            } else {
                                if (!isAllNullForPrefix(anyTuple, fieldName)) {
                                    Object nestedInstance = field.getType().getDeclaredConstructor().newInstance();
                                    buildOutDtoSafe(field.getType(), fieldName, anyTuple, nestedInstance, Collections.emptyMap());
                                    field.set(dto, nestedInstance);
                                }
                            }
                        }
                        return dto;
                    } catch (Exception e) {
                        throw new RuntimeException("Erro ao mapear Tuple para DTO", e);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <TInputDto extends QueryParams> List<TId> getPaginatedIds(TInputDto dtoIn, Class<?> entityClass) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TId> cq = cb.createQuery((Class<TId>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1]);
        Root<?> root = cq.from(entityClass);

        // Selecionar apenas o ID
        cq.select(root.get("id"));

        // Mapa de joins e lista de predicados
        Map<String, Join<?, ?>> joins = new HashMap<>();
        List<Predicate> predicates = new ArrayList<>();

        // Aplicar filtros do DTO de entrada
        buildPredicates(dtoIn, root, joins, predicates, cb, cq);

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        // Criar a query tipada
        TypedQuery<TId> query = em.createQuery(cq);

        // Aplicar paginação, se necessário
        if (!dtoIn.getPagination().getAllRows()) {
            int offset = dtoIn.getPagination().getOffset();
            int size = dtoIn.getPagination().getPageSize();
            query.setFirstResult(offset);
            query.setMaxResults(size);
        }

        return query.getResultList();
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


    private static void buildOutDto(Class<?> dtoClass, String prefix, Tuple tuple, Object dto, Map<String, List<Object>> collectionValues) {
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

        // Usar LinkedHashSet para manter ordem e evitar duplicatas
        Collection<Object> collectionInstance = new ArrayList<>();

        // Obter os IDs únicos da coleção
        List<Object> ids = collectionValues.getOrDefault(fieldPath, Collections.emptyList());

        // Mapear cada elemento da coleção
        for (Object id : new LinkedHashSet<>(ids)) { // Deduplicar IDs
            if (elementType.getName().startsWith("java.") || elementType.isPrimitive()) {
                collectionInstance.add(id);
                continue;
            }

            // Criar uma instância do elemento da coleção (ex.: TaskDto)
            Object elementInstance = elementType.getDeclaredConstructor().newInstance();

            // Mapear os campos do elemento usando a tupla original
            buildOutDto((Class<Object>) elementType, fieldPath, tuple, elementInstance, collectionValues);
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