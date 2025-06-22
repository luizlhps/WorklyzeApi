package com.worklyze.worklyze.application.dto.task;

import com.worklyze.worklyze.application.dto.demand.DemandCreateInDto;
import com.worklyze.worklyze.domain.entity.Demand;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.entity.TypeStatus;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@AutoMap(Task.class)
public class TaskCreateInDto {
    private String name;

    private DemandDto demand;

    private UserDto user;



    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @AutoMap(User.class)
    public static class UserDto {
        private UUID id;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @AutoMap(Demand.class)
    public static class DemandDto {
        private UUID id;
    }


}