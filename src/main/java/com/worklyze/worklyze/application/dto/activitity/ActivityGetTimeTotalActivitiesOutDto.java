package com.worklyze.worklyze.application.dto.activitity;

import com.worklyze.worklyze.domain.entity.Activity;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@AutoMap(Activity.class)
public class ActivityGetTimeTotalActivitiesOutDto {
    private Duration duration;
}
