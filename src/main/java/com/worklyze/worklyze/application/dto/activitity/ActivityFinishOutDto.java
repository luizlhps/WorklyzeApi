package com.worklyze.worklyze.application.dto.activitity;

import com.worklyze.worklyze.domain.entity.*;
import com.worklyze.worklyze.shared.annotation.AutoMap;
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
public class ActivityFinishOutDto {
    public Duration restTotal;
    public Duration timeTotal;
    public ActivityDto activityDto;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @AutoMap(Activity.class)
    public static class ActivityDto {
        private UUID id;

        private OffsetDateTime startTime;

        private OffsetDateTime endTime;

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
}
