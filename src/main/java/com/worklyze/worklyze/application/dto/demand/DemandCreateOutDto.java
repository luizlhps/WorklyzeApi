package com.worklyze.worklyze.application.dto.demand;

import com.worklyze.worklyze.domain.entity.*;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@AutoMap(Demand.class)
public class DemandCreateOutDto {
    private UUID id;

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String totalTime;

    private TypeStatusDto typeStatus;
    private UserDto user;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @AutoMap(TypeStatus.class)
    public static class TypeStatusDto {
        private Long id;
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
