package com.worklyze.worklyze.application.service;

import com.worklyze.worklyze.application.dto.activitity.*;
import com.worklyze.worklyze.application.dto.demand.DemandUpdateOutDto;
import com.worklyze.worklyze.application.dto.task.TaskGetTimeTotalInDto;
import com.worklyze.worklyze.application.dto.task.TaskUpdateOutDto;
import com.worklyze.worklyze.domain.entity.Activity;
import com.worklyze.worklyze.domain.entity.Demand;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.entity.TypeStatus;
import com.worklyze.worklyze.domain.enums.TypeStatusEnum;
import com.worklyze.worklyze.domain.interfaces.repository.ActivityRepository;
import com.worklyze.worklyze.domain.interfaces.services.ActivityService;
import com.worklyze.worklyze.domain.interfaces.services.DemandService;
import com.worklyze.worklyze.domain.interfaces.services.TaskService;
import com.worklyze.worklyze.infra.repository.ActivityRepositoryImpl;
import com.worklyze.worklyze.shared.exceptions.NotFoundException;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.worklyze.worklyze.adapter.exception.ActivityExceptionCode.ACTIVITY_ALREADY_ACTIVITY;
import static com.worklyze.worklyze.adapter.exception.ActivityExceptionCode.ACTIVITY_NOT_FOUND;
import static com.worklyze.worklyze.adapter.exception.TaskExceptionCode.TASK_NOT_FOUND;

@Service
public class ActivityServiceImpl extends BaseServiceImpl<Activity, UUID> implements ActivityService {
    private final TaskService taskService;
    private final ActivityRepository repository;
    private final DemandService demandService;


    public ActivityServiceImpl(ActivityRepositoryImpl repository, ModelMapper modelMapper, EntityManager entityManager, TaskService taskService, DemandService demandService) {
        super(repository, modelMapper, entityManager);
        this.taskService = taskService;
        this.repository = repository;
        this.demandService = demandService;
    }

    @Override
    public ActivityCreateOutDto create(ActivityCreateInDto dto) {
        var typeStatus = new TypeStatus();
        typeStatus.setId(TypeStatusEnum.ABERTO.getValue());

        var activeActivity = repository.getActive(dto.getUser().getId());

        if (activeActivity != null) {
            throw new NotFoundException(ACTIVITY_ALREADY_ACTIVITY.getMessage(), ACTIVITY_ALREADY_ACTIVITY.getCode());
        }

        Activity entity = modelMapper.map(dto, Activity.class);

        entity.setStartTime(OffsetDateTime.now());

        Activity tEntityCreated = repository.create(entity);

        return modelMapper.map(tEntityCreated, ActivityCreateOutDto.class);
    }

    @Override
    public ActivityFinishOutDto finish(ActivityFinishInDto dto) {
        Activity entity = findById(dto.getId(), Activity.class);

        if (entity == null) {
            throw new NotFoundException(ACTIVITY_NOT_FOUND.getMessage(), ACTIVITY_NOT_FOUND.getCode());
        }

        if (!dto.getUser().getId().equals(entity.getUser().getId())) {
            throw new NotFoundException(ACTIVITY_NOT_FOUND.getMessage(), ACTIVITY_NOT_FOUND.getCode());
        }

        modelMapper.map(dto, entity);

        update(dto, Activity.class, ActivityFinishOutDto.class);

        Task task = taskService.findById(dto.getTask().getId(), Task.class);

        if (task == null) {
            throw new NotFoundException(TASK_NOT_FOUND.getMessage(), TASK_NOT_FOUND.getCode());
        }

        LocalDateTime startDay = LocalDate.now().atStartOfDay();
        LocalDateTime endDay = LocalDate.now().atTime(23, 59, 59);
        ;

        Duration restTotal = repository.getTimeRestTotal(task.getId(), startDay, endDay);

        var taskUpdateOutDto = updateTotalTimeTask(task);

        updateTotalTimeDemand(task);

        var activityDto = modelMapper.map(entity, ActivityFinishOutDto.ActivityDto.class);

        return new ActivityFinishOutDto(restTotal, taskUpdateOutDto.getTimeTotal(), activityDto);
    }

    private TaskUpdateOutDto updateTotalTimeTask(Task task) {
        var totalTimeActivities = repository.getTimeTotalActivities(task.getId());
        task.setTimeTotal(totalTimeActivities);

        return taskService.update(task, Task.class, TaskUpdateOutDto.class);
    }

    private void updateTotalTimeDemand(Task task) {
        var taskGetTimeTotalInDto = new TaskGetTimeTotalInDto(task.getDemand().getId());
        var totalTimeTasks = taskService.getTimeTotal(taskGetTimeTotalInDto);

        var demand = demandService.findById(task.getDemand().getId(), Demand.class);
        demand.setTotalTime(totalTimeTasks.getDuration());

        demandService.update(demand, Demand.class, DemandUpdateOutDto.class);
    }

    @Override
    public ActivityGetTimeTotalActivitiesOutDto getTimeTotalActivities(ActivityGetTimeTotalActivitiesInDto dto) {
        return new ActivityGetTimeTotalActivitiesOutDto(repository.getTimeTotalActivities(dto.getTaskId()));
    }

    @Override
    public ActivityGetActiveOutDto getActive(ActivityGetActiveInDto dto) {
        Activity entity = repository.getActive(dto.getUser().getId());
        var activityDto = new ActivityGetActiveOutDto();

        if (entity == null) {
            activityDto.setActive(false);

            return activityDto;
        }

        activityDto.setId(entity.getId());
        activityDto.setTask(new ActivityGetActiveOutDto.TaskDto(entity.getTask().getId()));
        activityDto.setStartTime(entity.getStartTime());

        return activityDto;
    }

    @Override
    public ActivityGetByIdOutDto getById(UUID id, UUID userId) {
        var activityGetAllInDto = new ActivityGetAllInDto();
        var userDto = new ActivityGetAllInDto.UserDto(userId);

        activityGetAllInDto.setUser(userDto);
        activityGetAllInDto.setId(id);

        PageResult<ActivityGetAllOutDto> getAll = repository.findAll(activityGetAllInDto, ActivityGetAllOutDto.class);

        if (getAll.getItems().isEmpty()) {
            throw new NotFoundException(ACTIVITY_NOT_FOUND.getMessage(), ACTIVITY_NOT_FOUND.getCode());

        }

        return modelMapper.map(getAll.getItems().getFirst(), ActivityGetByIdOutDto.class);
    }

}
