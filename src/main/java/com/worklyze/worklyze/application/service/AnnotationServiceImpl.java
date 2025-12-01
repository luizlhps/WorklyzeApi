package com.worklyze.worklyze.application.service;

import com.worklyze.worklyze.domain.entity.Annotation;
import com.worklyze.worklyze.domain.interfaces.repository.AnnotationRepository;
import com.worklyze.worklyze.domain.interfaces.services.AnnotationService;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class AnnotationServiceImpl extends BaseServiceImpl<Annotation, Long> implements AnnotationService {
    public AnnotationServiceImpl(AnnotationRepository repository, ModelMapper modelMapper, EntityManager entityManager) {
        super(repository, modelMapper, entityManager);
    }
}
