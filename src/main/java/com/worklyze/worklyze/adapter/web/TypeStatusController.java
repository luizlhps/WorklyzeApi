package com.worklyze.worklyze.adapter.web;

import com.worklyze.worklyze.application.dto.task.TaskGetAllInDto;
import com.worklyze.worklyze.application.dto.task.TaskStatusUpdateInDto;
import com.worklyze.worklyze.application.dto.task.TaskStatusUpdateOutDto;
import com.worklyze.worklyze.application.dto.typestatus.TypeStatusActivitiesGetAllInputDto;
import com.worklyze.worklyze.application.dto.typestatus.TypeStatusActivitiesGetAllOutDto;
import com.worklyze.worklyze.application.dto.typestatus.TypeStatusGetAllInDto;
import com.worklyze.worklyze.application.dto.typestatus.TypeStatusGetAllOutDto;
import com.worklyze.worklyze.domain.interfaces.services.TypeStatusService;
import com.worklyze.worklyze.infra.config.security.CustomUserPrincipal;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/typestatus")
public class TypeStatusController {

    private final TypeStatusService typeStatusService;

    public TypeStatusController(TypeStatusService typeStatusService) {
        this.typeStatusService = typeStatusService;
    }

    @GetMapping("")
    public ResponseEntity<PageResult<TypeStatusGetAllOutDto>> getAll(
            @ParameterObject @ModelAttribute TypeStatusGetAllInDto dto) {
        PageResult<TypeStatusGetAllOutDto> getAll = typeStatusService.findAll(dto, TypeStatusGetAllOutDto.class);

        return ResponseEntity.ok(getAll);
    }

    @GetMapping("/activities")
    public ResponseEntity<PageResult<TypeStatusActivitiesGetAllOutDto>> getAllActivities(
            @ParameterObject @ModelAttribute TypeStatusActivitiesGetAllInputDto dto,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {

        PageResult<TypeStatusActivitiesGetAllOutDto> getAll = typeStatusService.findAll(dto, TypeStatusActivitiesGetAllOutDto.class);

        return ResponseEntity.ok(getAll);
    }
}
