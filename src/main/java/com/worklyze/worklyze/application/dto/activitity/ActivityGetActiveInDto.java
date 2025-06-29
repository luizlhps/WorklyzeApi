package com.worklyze.worklyze.application.dto.activitity;

import com.worklyze.worklyze.domain.entity.Activity;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@AutoMap(Activity.class)
public class ActivityGetActiveInDto {
    private UserDto user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @AutoMap(User.class)
    public static class UserDto {
        private UUID id;
    }
}
