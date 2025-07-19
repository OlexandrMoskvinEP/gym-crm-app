package com.gym.crm.app.mapper;

import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.rest.TraineeTrainingGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingMapper {
    @Mapping(target = "trainee", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "trainingType", ignore = true)
    Training toEntity(TrainingSaveRequest dto);

    @Mapping(source = "trainee.id", target = "traineeId")
    @Mapping(source = "trainer.id", target = "trainerId")
    TrainingDto toDto(Training training);

    @Mapping(source = "trainingType.trainingTypeName", target = "trainingType")
    @Mapping(target = "trainerName", ignore = true)
    @Mapping(target = "trainingDuration", expression = "java(training.getTrainingDuration().intValue())")
    TraineeTrainingGetResponse toTraineeTrainingResponse(TrainingDto training);
}
