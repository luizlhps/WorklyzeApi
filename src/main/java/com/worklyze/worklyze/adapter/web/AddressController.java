package com.worklyze.worklyze.adapter.web;

import com.worklyze.worklyze.application.dto.address.AddressDto;
import com.worklyze.worklyze.application.dto.address.AddressGetAllInDto;
import com.worklyze.worklyze.application.dto.address.AddressGetAllOutDto;
import com.worklyze.worklyze.application.service.AddressService;
import com.worklyze.worklyze.domain.entity.Address;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import org.modelmapper.ModelMapper;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;
    private final ModelMapper modelMapper;

    public AddressController(AddressService addressService, ModelMapper modelMapper) {
        this.addressService = addressService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<Address> create(@RequestBody AddressDto dto) {
        Address entity = modelMapper.map(dto, Address.class);
        Address created = addressService.create(entity, Address.class, Address.class);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> update(@PathVariable UUID id, @RequestBody AddressDto dto) {
        Address entity = modelMapper.map(dto, Address.class);
        entity.setId(id);
        Address updated = addressService.update(entity, Address.class, Address.class);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("")
    public ResponseEntity<PageResult<AddressGetAllOutDto>> update(
            @ParameterObject @ModelAttribute AddressGetAllInDto dto) {
        var updated = addressService.findAll(dto, AddressGetAllOutDto.class);
        return ResponseEntity.ok(updated);
    }
}
