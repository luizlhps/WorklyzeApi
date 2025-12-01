package com.worklyze.worklyze.application.dto.annotation;

import com.worklyze.worklyze.domain.entity.Annotation;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AutoMap(Annotation.class)
public class AnnotationCreateInDto {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Text block is required")
    private String textBlock;
}
