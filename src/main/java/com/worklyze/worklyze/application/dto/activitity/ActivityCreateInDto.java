package com.worklyze.worklyze.application.dto.activitity;

import com.worklyze.worklyze.domain.entity.Activity;
import com.worklyze.worklyze.domain.entity.Demand;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

//todo adicionar validação para n passar do horario atual e n deixar passar o end time ser menor que o start time


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@AutoMap(Activity.class)
public class ActivityCreateInDto {
    private OffsetDateTime startTime;

    private String name;

    private TaskDto task;

    private UserDto user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @AutoMap(Task.class)
    public static class TaskDto {
        private UUID id;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @AutoMap(User.class)
    public static class UserDto {
        private UUID id;
    }

}