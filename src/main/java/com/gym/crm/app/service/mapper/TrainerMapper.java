package com.gym.crm.app.service.mapper;

import com.gym.crm.app.domain.model.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {
    public static Trainer mapToEntityWithUserId(Trainer source, int userId) {
        return source.toBuilder()
                .userId(userId)
                .build();
    }
}
