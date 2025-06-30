package com.gym.crm.app.service.mapper;

import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.domain.model.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {
    public static Training mapDtoToEntity(TrainingDto source) {
        return Training.builder()
                .trainerId(source.getTrainerId())
                .traineeId(source.getTraineeId())
                .trainingDate(source.getTrainingDate())
                .trainingType(source.getTrainingType())
                .trainingDuration(source.getTrainingDuration())
                .trainingName(source.getTrainingName())
                .build();
    }
}