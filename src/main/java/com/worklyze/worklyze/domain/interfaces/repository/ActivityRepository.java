package com.worklyze.worklyze.domain.interfaces.repository;

import com.worklyze.worklyze.domain.entity.Activity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository extends BaseRepository<Activity, UUID> {
    Duration getTimeTotalActivities(UUID taskId);
    Duration getTimeRestTotal(UUID taskId, LocalDateTime startDay, LocalDateTime endDay);
    Activity getActive(UUID user);

}
