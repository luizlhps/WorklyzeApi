package com.worklyze.worklyze.infra.repository;

import com.worklyze.worklyze.domain.entity.Activity;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.interfaces.repository.ActivityRepository;
import com.worklyze.worklyze.domain.interfaces.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class ActivityRepositoryImpl extends BaseRepositoryImpl<Activity, UUID> implements ActivityRepository {
    public ActivityRepositoryImpl(EntityManager em, ModelMapper modelMapper) {
        super(em, modelMapper, Activity.class);
    }

    public Duration getTimeTotalActivities(UUID taskId) {
        String sql = """
                SELECT COALESCE (
                            SUM(
                                EXTRACT(EPOCH FROM (COALESCE(end_time, now())) - start_time
                            )
                        ), 0)
                FROM activity
                WHERE activity.task_id = :taskId""";

        Number totalSeconds = (Number) em.createNativeQuery(sql)
                .setParameter("taskId", taskId)
                .getSingleResult();

        return Duration.ofSeconds(totalSeconds.longValue());
    }

    public Duration getTimeRestTotal(UUID taskId, LocalDateTime startDay, LocalDateTime endDay) {
        String sql = """
                   WITH ordered AS (
                       SELECT
                           id,
                           start_time,
                           end_time,
                           LAG(end_time) OVER (ORDER BY start_time) AS prev_end
                       FROM public.activity
                        WHERE activity.task_id = :taskId
                        AND activity.start_time BETWEEN :startDay AND :endDay
                   )
                   SELECT
                       COALESCE(EXTRACT(EPOCH FROM start_time - prev_end), 0) AS interval_in_seg
                   FROM ordered
                   WHERE prev_end IS NOT NULL
                   ORDER BY interval_in_seg DESC
                   LIMIT 1;
                """;

            Number totalSeconds = (Number) em.createNativeQuery(sql)
                    .setParameter("taskId", taskId)
                    .setParameter("startDay", startDay)
                    .setParameter("endDay", endDay)
                    .getSingleResult();

            if (totalSeconds == null) {
                return Duration.ZERO;
            }

            return Duration.ofSeconds(totalSeconds.longValue());
    }
}
