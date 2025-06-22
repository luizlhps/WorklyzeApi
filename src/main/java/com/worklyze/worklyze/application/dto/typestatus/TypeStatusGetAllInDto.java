package com.worklyze.worklyze.application.dto.typestatus;

import com.worklyze.worklyze.domain.entity.TypeStatus;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import com.worklyze.worklyze.shared.page.QueryParamsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AutoMap(TypeStatus.class)
public class TypeStatusGetAllInDto extends QueryParamsImpl {
    private Long id;
    private String name;
}
