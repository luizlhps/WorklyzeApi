package com.worklyze.worklyze.application.dto.task;

import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@AutoMap(Task.class)
public class TaskGetTimeTotalInDto {
    private UUID demandId;
}
