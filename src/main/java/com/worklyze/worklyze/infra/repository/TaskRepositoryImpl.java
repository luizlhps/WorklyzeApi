package com.worklyze.worklyze.infra.repository;

import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.interfaces.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
public class TaskRepositoryImpl extends BaseRepositoryImpl<Task, UUID> implements TaskRepository {
    public TaskRepositoryImpl(EntityManager em, ModelMapper modelMapper) {
        super(em, modelMapper, Task.class);
    }

    @Override
    public Duration getTotalTime(UUID demandId) {
        String sql = """
                SELECT COALESCE(SUM(t.time_total), 0)
                FROM task t
                WHERE t.demand_id = :demandId""";

        Number totalSeconds = (Number) em.createNativeQuery(sql)
                .setParameter("demandId", demandId)
                .getSingleResult();

        return Duration.ofSeconds(totalSeconds.longValue());
    }
}
