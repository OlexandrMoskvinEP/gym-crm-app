package com.gym.crm.app.mapper;

import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingMapper {
    @Mapping(source = "trainee.id", target = "traineeId")
    @Mapping(source = "trainer.id", target = "trainerId")
    TrainingDto toDto(Training training);
}
