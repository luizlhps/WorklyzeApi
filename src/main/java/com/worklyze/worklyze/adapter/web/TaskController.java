package com.worklyze.worklyze.adapter.web;

import com.worklyze.worklyze.application.dto.task.*;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.interfaces.services.TaskService;
import com.worklyze.worklyze.infra.config.security.CustomUserPrincipal;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskCreateOutDto> create(
            @RequestBody TaskCreateInDto dto,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        var userId = user.getId();
        var taskCreateInDto = new TaskCreateInDto.UserDto();
        taskCreateInDto.setId(userId);
        dto.setUser(taskCreateInDto);

        var created = taskService.create(dto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskSimpleUpdateOutDto> update(
            @PathVariable UUID id,
            @RequestBody TaskSimpleUpdateInDto dto,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        dto.setId(id);

        var userId = user.getId();
        var taskSimpleUpdateDto = new TaskSimpleUpdateInDto.UserDto();
        taskSimpleUpdateDto.setId(userId);
        dto.setUser(taskSimpleUpdateDto);

        TaskSimpleUpdateOutDto updated = taskService.updateSimple(dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("")
    public ResponseEntity<PageResult<TaskGetAllOutDto>> getAll(
            @ParameterObject @ModelAttribute TaskGetAllInDto dto) {
        PageResult<TaskGetAllOutDto> getAll = taskService.findAll(dto, TaskGetAllOutDto.class);
        return ResponseEntity.ok(getAll);
    }

}
