package com.worklyze.worklyze.application.service;

import com.worklyze.worklyze.application.dto.task.*;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.entity.TypeStatus;
import com.worklyze.worklyze.domain.enums.TypeStatusEnum;
import com.worklyze.worklyze.domain.interfaces.repository.TaskRepository;
import com.worklyze.worklyze.domain.interfaces.services.TaskService;
import com.worklyze.worklyze.shared.exceptions.NotFoundException;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

import static com.worklyze.worklyze.adapter.exception.TaskExceptionCode.TASK_NOT_FOUND;

@Service
public class TaskServiceImpl extends BaseServiceImpl<Task, UUID> implements TaskService {
    private final TaskRepository repository;

    public TaskServiceImpl(TaskRepository repository, ModelMapper modelMapper, EntityManager entityManager) {
        super(repository, modelMapper, entityManager);
        this.repository = repository;
    }

    @Override
    public TaskCreateOutDto create(TaskCreateInDto dto) {
        var typeStatus = new TypeStatus();
        typeStatus.setId(TypeStatusEnum.ABERTO.getValue());

        Task entity = modelMapper.map(dto, Task.class);

        entity.setRestTotal(Duration.ZERO);
        entity.setTimeTotal(Duration.ZERO);
        entity.setTypeStatus(typeStatus);

        Task tEntityCreated = repository.create(entity);

        return modelMapper.map(tEntityCreated, TaskCreateOutDto.class);
    }

    @Override
    public TaskSimpleUpdateOutDto updateSimple(TaskSimpleUpdateInDto dto) {
        Task entity = findById(dto.getId(), Task.class);

        if (entity == null) {
            throw new NotFoundException(TASK_NOT_FOUND.getMessage(), TASK_NOT_FOUND.getCode());
        }

        if (!dto.getUser().getId().equals(entity.getUser().getId())) {
            throw new NotFoundException(TASK_NOT_FOUND.getMessage(), TASK_NOT_FOUND.getCode());
        }

        return update(dto, Task.class, TaskSimpleUpdateOutDto.class);
    }

    @Override
    public TaskGetTimeTotalOutDto getTimeTotal(TaskGetTimeTotalInDto dto) {
        return new TaskGetTimeTotalOutDto(repository.getTotalTime(dto.getDemandId()));
    }

}
