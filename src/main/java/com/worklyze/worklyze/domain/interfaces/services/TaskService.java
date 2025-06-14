package com.worklyze.worklyze.domain.interfaces.services;

import com.worklyze.worklyze.application.dto.task.*;
import com.worklyze.worklyze.domain.entity.Task;

import java.util.UUID;

public interface TaskService extends BaseService<Task, UUID> {
    TaskCreateOutDto create(TaskCreateInDto dto);
    TaskSimpleUpdateOutDto updateSimple(TaskSimpleUpdateInDto dto);
    TaskGetTimeTotalOutDto getTimeTotal(TaskGetTimeTotalInDto dto);
}
