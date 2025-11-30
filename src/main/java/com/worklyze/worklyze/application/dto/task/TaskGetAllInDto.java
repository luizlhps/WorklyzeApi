package com.worklyze.worklyze.application.dto.task;

import com.worklyze.worklyze.domain.entity.Demand;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.entity.TypeStatus;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.shared.annotation.AttributeNameAnnotation;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import com.worklyze.worklyze.shared.annotation.InAnnotation;
import com.worklyze.worklyze.shared.annotation.NotInAnnotation;
import com.worklyze.worklyze.shared.page.QueryParamsImpl;
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
@AutoMap(Task.class)
public class TaskGetAllInDto extends QueryParamsImpl {

    @InAnnotation(Task.class)
    private List<UUID> id;

    private String name;

    private Duration timeTotal;

    private Duration restTotal;

    private DemandDto demand;

    private TypeStatusDto typeStatus;

    private UserDto user;

    private Boolean deleted = false;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @AutoMap(Demand.class)
    public static class DemandDto {
        private UUID id;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @AutoMap(TypeStatus.class)
    public static class TypeStatusDto {
        private Long id;

        @AttributeNameAnnotation("id")
        @NotInAnnotation(Task.class)
        private List<Long> idNot;
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