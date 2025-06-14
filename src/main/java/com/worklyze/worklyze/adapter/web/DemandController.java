package com.worklyze.worklyze.adapter.web;

import com.worklyze.worklyze.application.dto.address.AddressGetAllInDto;
import com.worklyze.worklyze.application.dto.address.AddressGetAllOutDto;
import com.worklyze.worklyze.application.dto.demand.DemandCreateInDto;
import com.worklyze.worklyze.application.dto.demand.DemandCreateOutDto;
import com.worklyze.worklyze.application.dto.demand.DemandGetAllInDto;
import com.worklyze.worklyze.application.dto.demand.DemandGetAllOutDto;
import com.worklyze.worklyze.domain.entity.Demand;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.domain.interfaces.services.DemandService;
import com.worklyze.worklyze.infra.config.security.CustomUserPrincipal;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demands")
public class DemandController {

    private final DemandService demandService;

    public DemandController(DemandService demandService) {
        this.demandService = demandService;
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

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

/*
    @PutMapping("/{id}")
    public ResponseEntity<Demand> update(@PathVariable UUID id, @RequestBody DemandDto dto) {
        Demand entity = modelMapper.map(dto, Demand.class);
        entity.setId(id);
        Demand updated = demandService.update(entity, Demand.class, Demand.class);
        return ResponseEntity.ok(updated);
    }
*/

    @GetMapping("")
    public ResponseEntity<PageResult<DemandGetAllOutDto>> getAll(
            @ParameterObject @ModelAttribute DemandGetAllInDto dto) {
        PageResult<DemandGetAllOutDto> getAll = demandService.findAll(dto, DemandGetAllOutDto.class);
        return ResponseEntity.ok(getAll);
    }

}
