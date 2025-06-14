package com.worklyze.worklyze.application.dto.demand;

import com.worklyze.worklyze.domain.entity.Demand;
import com.worklyze.worklyze.domain.entity.TypeStatus;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@AutoMap(Demand.class)
public class DemandUpdateOutDto {
    private UUID id;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private Duration totalTime;

    private TypeStatusDto typeStatus;

    private UserDto user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @AutoMap(TypeStatus.class)
    public static class TypeStatusDto {
        private Long id;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @AutoMap(User.class)
    public static class UserDto {
        private UUID id;
    }
}
