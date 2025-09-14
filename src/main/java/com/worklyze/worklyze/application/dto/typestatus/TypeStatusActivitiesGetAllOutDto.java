package com.worklyze.worklyze.application.dto.typestatus;

import com.worklyze.worklyze.domain.entity.Demand;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.entity.TypeStatus;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AutoMap(TypeStatus.class)
public class TypeStatusActivitiesGetAllOutDto {

    private Long id;

    private String name;

    private List<TaskDto> tasks = new ArrayList<>();

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @AutoMap(Task.class)
    public static class TaskDto {
        private UUID id;

        private String name;

        private Duration timeTotal;

        private Duration restTotal;

        private DemandDto demand;

        private Boolean deleted;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @AutoMap(Demand.class)
        public static class DemandDto {
            private UUID id;
            private String name;
        }
    }
}
