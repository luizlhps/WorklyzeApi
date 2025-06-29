package com.worklyze.worklyze.application.dto.demand;

import com.worklyze.worklyze.domain.entity.Demand;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.entity.TypeStatus;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@AutoMap(Demand.class)
public class DemandGetAllOutDto  {

    private UUID id;

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Duration totalTime;

    private TypeStatusDto typeStatus;
    private UserDto user;

    private List<TaskDto> tasks;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @AutoMap(TypeStatus.class)
    public static class TypeStatusDto {
        private Long id;
        private String name;
    }

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
    @AutoMap(Task.class)
    public static class TaskDto {
        private UUID id;
        private String name;
        private TypeStatusDto typeStatus;
    }
}