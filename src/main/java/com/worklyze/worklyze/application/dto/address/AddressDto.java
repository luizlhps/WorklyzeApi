package com.worklyze.worklyze.application.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "DTO para transferência de dados da entidade Address")
public class AddressDto {

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
    private UUID userId;
}