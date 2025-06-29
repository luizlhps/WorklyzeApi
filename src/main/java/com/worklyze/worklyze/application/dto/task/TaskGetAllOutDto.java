package com.worklyze.worklyze.application.dto.task;

import com.worklyze.worklyze.domain.entity.*;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@AutoMap(Task.class)
public class TaskGetAllOutDto {
    private UUID id;

    private String name;

    private Duration timeTotal;

    private Duration restTotal;

    private DemandDto demand;

    private TypeStatusDto typeStatus;

    private List<ActivityDto> activities;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @AutoMap(Demand.class)
    public static class DemandDto {
        private UUID id;
        private String name;
    }

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
    @AutoMap(TypeStatus.class)
    public static class ActivityDto {
        private UUID id;
        private OffsetDateTime startTime;
        private OffsetDateTime endTime;
        private String name;
    }

}