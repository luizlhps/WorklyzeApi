package com.worklyze.worklyze.application.dto.annotation;

import com.worklyze.worklyze.domain.entity.Annotation;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AutoMap(Annotation.class)
public class AnnotationGetAllOutDto {
    private Long id;
    private String name;
    private String textBlock;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
