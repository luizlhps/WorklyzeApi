package com.worklyze.worklyze.application.service;

import com.worklyze.worklyze.application.dto.UserMeInDto;
import com.worklyze.worklyze.application.dto.UserMeOutDto;
import com.worklyze.worklyze.application.dto.activitity.*;
import com.worklyze.worklyze.application.dto.demand.DemandUpdateOutDto;
import com.worklyze.worklyze.application.dto.task.TaskGetTimeTotalInDto;
import com.worklyze.worklyze.application.dto.task.TaskUpdateOutDto;
import com.worklyze.worklyze.domain.entity.*;
import com.worklyze.worklyze.domain.enums.TypeStatusEnum;
import com.worklyze.worklyze.domain.interfaces.repository.ActivityRepository;
import com.worklyze.worklyze.domain.interfaces.repository.BaseRepository;
import com.worklyze.worklyze.domain.interfaces.repository.UserRepository;
import com.worklyze.worklyze.domain.interfaces.services.ActivityService;
import com.worklyze.worklyze.domain.interfaces.services.DemandService;
import com.worklyze.worklyze.domain.interfaces.services.TaskService;
import com.worklyze.worklyze.domain.interfaces.services.UserService;
import com.worklyze.worklyze.infra.repository.ActivityRepositoryImpl;
import com.worklyze.worklyze.infra.repository.UserRepositoryImpl;
import com.worklyze.worklyze.shared.exceptions.NotFoundException;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.worklyze.worklyze.adapter.exception.ActivityExceptionCode.ACTIVITY_NOT_FOUND;
import static com.worklyze.worklyze.adapter.exception.TaskExceptionCode.TASK_NOT_FOUND;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, UUID> implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository, ModelMapper modelMapper, EntityManager entityManager) {
        super(repository, modelMapper, entityManager);
        this.repository = repository;
    }

    public UserMeOutDto getMe(UserMeInDto userMeInDto) {
        User user = repository.findByEmail(userMeInDto.getEmail()).orElseThrow(() -> new NotFoundException("Usuario nao encontrado", null));
        return modelMapper.map(user, UserMeOutDto.class);
    }

}
