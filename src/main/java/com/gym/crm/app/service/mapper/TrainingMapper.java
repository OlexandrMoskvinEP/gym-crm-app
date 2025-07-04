package com.gym.crm.app.service.mapper;

import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.domain.model.Training;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TrainingMapper {
    public static Training mapDtoToEntity(TrainingDto source) {
        return Training.builder()
                .trainer(source.getTrainer())
                .trainee(source.getTrainee())
                .trainingDate(source.getTrainingDate())
                .trainingType(source.getTrainingType())
                .trainingDuration(BigDecimal.valueOf(source.getTrainingDuration()))
                .trainingName(source.getTrainingName())
                .build();
    }
}