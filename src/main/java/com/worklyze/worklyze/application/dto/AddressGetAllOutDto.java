package com.worklyze.worklyze.application.dto;

import com.worklyze.worklyze.domain.entity.Address;
import com.worklyze.worklyze.domain.entity.BaseEntity;
import com.worklyze.worklyze.domain.entity.TypeProvider;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class AddressGetAllOutDto {
    @Schema(description = "Identificador único do endereço", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Cidade do endereço", example = "São Paulo")
    private String city;

    @Schema(description = "Rua do endereço", example = "Av. Paulista")
    private String street;

    @Schema(description = "Estado do endereço", example = "SP")
    private String state;

    @Schema(description = "CEP do endereço", example = "01311-000")
    private String zip;


    @Schema(description = "Identificador do usuário relacionado", example = "8a7b5a0d-1f4b-4d9e-a15b-3245f2a23b8c")
    private UserDto user;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @AutoMap(User.class)
    public static class UserDto {
        private UUID id;

        private TypeProviderDto typeProvider;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @AutoMap(TypeProvider.class)
    public static class TypeProviderDto  {
        private Long id;

        private String nome;
    }
}
