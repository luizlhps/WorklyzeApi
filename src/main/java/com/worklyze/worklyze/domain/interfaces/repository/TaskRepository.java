package com.worklyze.worklyze.domain.interfaces.repository;

import com.worklyze.worklyze.domain.entity.Task;

import java.time.Duration;
import java.util.UUID;

public interface TaskRepository extends BaseRepository<Task, UUID> {
    Duration getTotalTime(UUID demandId);
}
