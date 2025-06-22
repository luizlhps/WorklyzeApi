package com.worklyze.worklyze.application.dto.typestatus;

import com.worklyze.worklyze.domain.entity.TypeStatus;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AutoMap(TypeStatus.class)
public class TypeStatusGetAllOutDto {

    private Long id;

    private String name;
}
