package com.worklyze.worklyze.application.dto.activitity;

import com.worklyze.worklyze.domain.entity.Activity;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@AutoMap(Activity.class)
public class ActivityGetActiveOutDto {
    private UUID id;
    private TaskDto task;
    boolean active;
    private OffsetDateTime startTime;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @AutoMap(Task.class)
    public static class TaskDto {
        private UUID id;
    }
}
