package com.worklyze.worklyze.infra.repository;

import com.worklyze.worklyze.domain.entity.Annotation;
import com.worklyze.worklyze.domain.interfaces.repository.AnnotationRepository;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AnnotationRepositoryImpl extends BaseRepositoryImpl<Annotation, Long> implements AnnotationRepository {
    public AnnotationRepositoryImpl(EntityManager em, ModelMapper modelMapper) {
        super(em, modelMapper, Annotation.class);
    }
}
