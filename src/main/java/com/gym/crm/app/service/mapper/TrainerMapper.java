package com.gym.crm.app.service.mapper;

import com.gym.crm.app.domain.model.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {
    public static Trainer mapToEntityWithUserId(Trainer source, long userId) {
        return source.toBuilder()
                .id(userId)
                .build();
    }
}
