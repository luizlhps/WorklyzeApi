package com.worklyze.worklyze.application.dto.typestatus;

import com.worklyze.worklyze.domain.entity.*;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import com.worklyze.worklyze.shared.page.QueryParamsImpl;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AutoMap(TypeStatus.class)
@Builder
public class TypeStatusActivitiesGetAllInputDto extends QueryParamsImpl {

    private Long id;

    private String name;

    private TaskDto tasks;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @AutoMap(Task.class)
    @Builder
    public static class TaskDto {
        private UUID id;

        private String name;

        private Duration timeTotal;

        private Duration restTotal;

        private DemandDto demand;

        private UserDto user;

        private Boolean deleted = false;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @AutoMap(User.class)
        @Builder
        public static class UserDto {
            private UUID id;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @AutoMap(Demand.class)
        @Builder
        public static class DemandDto {
            private UUID id;
            private String name;
        }
    }
}
