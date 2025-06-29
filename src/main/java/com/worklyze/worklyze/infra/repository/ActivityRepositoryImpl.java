package com.worklyze.worklyze.infra.repository;

import com.worklyze.worklyze.domain.entity.Activity;
import com.worklyze.worklyze.domain.interfaces.repository.ActivityRepository;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
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
                    SELECT COALESCE((
                    SELECT EXTRACT(EPOCH FROM start_time - prev_end)
                    FROM (
                        SELECT
                            start_time,
                            LAG(end_time) OVER (ORDER BY start_time) AS prev_end
                        FROM public.activity
                        WHERE task_id = :taskId
                        AND start_time BETWEEN :startDay AND :endDay
                    ) ordered
                    WHERE prev_end IS NOT NULL
                    ORDER BY EXTRACT(EPOCH FROM start_time - prev_end) DESC
                    LIMIT 1
                ), 0) AS interval_in_seg;
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
    @Override
    public Activity getActive(UUID user) {
        String sql = """
                   SELECT a from Activity a  join fetch a.task where a.user.id = :user and a.endTime is null
                """;

        var activityResult = em.createQuery(sql, Activity.class)
                .setParameter("user", user)
                .getResultList();

        return activityResult.stream().findFirst().orElse(null);
    }
}
