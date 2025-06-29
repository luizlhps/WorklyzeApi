package com.worklyze.worklyze.adapter.web;

import com.worklyze.worklyze.application.dto.activitity.*;
import com.worklyze.worklyze.domain.interfaces.services.ActivityService;
import com.worklyze.worklyze.infra.config.security.CustomUserPrincipal;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import jakarta.transaction.Transactional;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/v1/activities")
public class ActivityController {

    private final ActivityService activitityService;

    public ActivityController(ActivityService activitityService) {
        this.activitityService = activitityService;
    }

    @PostMapping
    public ResponseEntity<ActivityCreateOutDto> create(
            @RequestBody ActivityCreateInDto dto,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        var userId = user.getId();
        var activitityCreateInDto = new ActivityCreateInDto.UserDto();
        activitityCreateInDto.setId(userId);
        dto.setUser(activitityCreateInDto);

        var created = activitityService.create(dto);

        var uri = UriComponentsBuilder.fromPath("{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ActivityFinishOutDto> finish(
            @PathVariable UUID id,
            @RequestBody ActivityFinishInDto dto,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        dto.setId(id);

        var userId = user.getId();
        var activititySimpleUpdateDto = new ActivityFinishInDto.UserDto();
        activititySimpleUpdateDto.setId(userId);
        dto.setUser(activititySimpleUpdateDto);
        ActivityFinishOutDto updated = activitityService.finish(dto);

        return ResponseEntity.ok(updated);
    }

    @GetMapping("")
    public ResponseEntity<PageResult<ActivityGetAllOutDto>> getAll(
            @ParameterObject @ModelAttribute ActivityGetAllInDto dto,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        var userDto = new ActivityGetAllInDto.UserDto(user.getId());
        dto.setUser(userDto);

        PageResult<ActivityGetAllOutDto> getAll = activitityService.findAll(dto, ActivityGetAllOutDto.class);

        return ResponseEntity.ok(getAll);
    }

    @GetMapping("{id}")
    public ResponseEntity<ActivityGetByIdOutDto> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {

        return ResponseEntity.ok(activitityService.getById(id, user.getId()));
    }

    @GetMapping("/active")
    public ResponseEntity<ActivityGetActiveOutDto> getActive(
            @AuthenticationPrincipal CustomUserPrincipal user) {

        var userId = user.getId();
        ActivityGetActiveInDto dto = new ActivityGetActiveInDto();
        dto.setUser(new ActivityGetActiveInDto.UserDto(userId));

        return ResponseEntity.ok(activitityService.getActive(dto));
    }

}
