package com.worklyze.worklyze.application.dto.annotation;

import com.worklyze.worklyze.domain.entity.Address;
import com.worklyze.worklyze.domain.entity.Annotation;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import com.worklyze.worklyze.shared.page.PageListImpl;
import com.worklyze.worklyze.shared.page.QueryParamsImpl;
import com.worklyze.worklyze.shared.page.interfaces.QueryParams;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AutoMap(Annotation.class)
public class AnnotationGetAllInDto extends QueryParamsImpl {
    private Long id;
    private String name;
    private String textBlock;
}
