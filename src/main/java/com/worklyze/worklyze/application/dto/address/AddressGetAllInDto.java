package com.worklyze.worklyze.application.dto.address;

import com.worklyze.worklyze.domain.entity.Address;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import com.worklyze.worklyze.shared.page.QueryParamsImpl;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@AutoMap(Address.class)
public class AddressGetAllInDto extends QueryParamsImpl {
    private UUID id;

    @Schema(description = "Cidade do endereço", example = "São Paulo")
    private String city;
}
