package com.worklyze.worklyze.adapter.web;

import com.worklyze.worklyze.application.dto.address.AddressGetAllInDto;
import com.worklyze.worklyze.application.dto.address.AddressGetAllOutDto;
import com.worklyze.worklyze.application.dto.demand.*;
import com.worklyze.worklyze.domain.entity.Demand;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.domain.interfaces.services.DemandService;
import com.worklyze.worklyze.infra.config.security.CustomUserPrincipal;
import com.worklyze.worklyze.shared.exceptions.NotFoundException;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.UUID;

import static com.worklyze.worklyze.adapter.exception.DemandExceptionCode.DEMAND_NOT_FOUND;

@RestController
@RequestMapping("/v1/demands")
public class DemandController {

    private final DemandService demandService;
    private final ModelMapper modelMapper;

    public DemandController(DemandService demandService, ModelMapper modelMapper) {
        this.demandService = demandService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<DemandCreateOutDto> create(
            @RequestBody DemandCreateInDto dto,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {

        var userDto = new DemandCreateInDto.UserDto();
        userDto.setId(user.getId());

        dto.setUser(userDto);

        var created = demandService.create(dto);

        var uri = UriComponentsBuilder.fromPath("/demands/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DemandUpdateOutDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid DemandUpdateInDto dto,
            @AuthenticationPrincipal CustomUserPrincipal user) {
        dto.setId(id);
        dto.setUser(new DemandUpdateInDto.UserDto(user.getId()));

        var updated = demandService.update(dto, Demand.class, DemandUpdateOutDto.class);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandGetIdOutDto> getById(@PathVariable UUID id) {
        DemandGetIdOutDto demand = demandService.findById(id, DemandGetIdOutDto.class);

        if (demand == null) {
            throw new NotFoundException(DEMAND_NOT_FOUND.getMessage(), DEMAND_NOT_FOUND.getCode());
        }

        return ResponseEntity.ok(demand);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        DemandGetIdOutDto demand = demandService.findById(id, DemandGetIdOutDto.class);

        if (demand == null) {
            throw new NotFoundException(DEMAND_NOT_FOUND.getMessage(), DEMAND_NOT_FOUND.getCode());
        }

        demandService.delete(id);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("")
    public ResponseEntity<PageResult<DemandGetAllOutDto>> getAll(
            @ParameterObject @ModelAttribute DemandGetAllInDto dto,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        dto.setUser(new DemandGetAllInDto.UserDto(user.getId()));

        PageResult<DemandGetAllOutDto> getAll = demandService.findAll(dto, DemandGetAllOutDto.class);
        return ResponseEntity.ok(getAll);
    }

}
