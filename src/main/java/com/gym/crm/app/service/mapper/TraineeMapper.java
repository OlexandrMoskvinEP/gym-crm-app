package com.gym.crm.app.service.mapper;

import com.gym.crm.app.domain.model.Trainee;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper {
    public static Trainee mapWithUserId(Trainee source, int userId) {
        return source.toBuilder()
                .userId(userId)
                .build();
    }
}