package com.worklyze.worklyze.adapter.web;

import com.worklyze.worklyze.application.dto.annotation.AnnotationCreateInDto;
import com.worklyze.worklyze.application.dto.annotation.AnnotationGetAllInDto;
import com.worklyze.worklyze.application.dto.annotation.AnnotationGetAllOutDto;
import com.worklyze.worklyze.domain.entity.Annotation;
import com.worklyze.worklyze.domain.interfaces.services.AnnotationService;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/annotations")
public class AnnotationController {

    private final AnnotationService annotationService;

    public AnnotationController(AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    @PostMapping
    public ResponseEntity<AnnotationGetAllOutDto> create(
            @RequestBody @Valid AnnotationCreateInDto dto,
            UriComponentsBuilder uriBuilder) {
        
        AnnotationGetAllOutDto created = annotationService.create(dto, Annotation.class, AnnotationGetAllOutDto.class);
        
        URI uri = uriBuilder.path("/v1/annotations/{id}")
                .buildAndExpand(created.getId())
                .toUri();
                
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnotationGetAllOutDto> getById(@PathVariable Long id) {
        AnnotationGetAllOutDto annotation = annotationService.findById(id, AnnotationGetAllOutDto.class);
        return ResponseEntity.ok(annotation);
    }

    @GetMapping
    public ResponseEntity<PageResult<AnnotationGetAllOutDto>> getAll(
            @ParameterObject @ModelAttribute AnnotationGetAllInDto dto) {
        
        PageResult<AnnotationGetAllOutDto> result = annotationService.findAll(dto, AnnotationGetAllOutDto.class);
        return ResponseEntity.ok(result);
    }
}
