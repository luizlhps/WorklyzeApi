package com.worklyze.worklyze.domain.interfaces.services;

import com.worklyze.worklyze.application.dto.activitity.*;
import com.worklyze.worklyze.domain.entity.Activity;

import java.util.UUID;

public interface ActivityService extends BaseService<Activity, UUID> {
    ActivityCreateOutDto create(ActivityCreateInDto dto);
    ActivityFinishOutDto finish(ActivityFinishInDto dto);
    ActivityGetTimeTotalActivitiesOutDto getTimeTotalActivities(ActivityGetTimeTotalActivitiesInDto dto);
    ActivityGetActiveOutDto getActive(ActivityGetActiveInDto dto);
    ActivityGetByIdOutDto getById(UUID id, UUID userId);
}
