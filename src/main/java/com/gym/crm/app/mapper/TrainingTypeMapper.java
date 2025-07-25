package com.gym.crm.app.mapper;


import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.rest.TrainingTypeRestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {
    @Mapping(source = "trainingTypeName", target = "trainingType")
    @Mapping(source = "id", target = "trainingTypeId")
    TrainingTypeRestDto toRestTrainingType(TrainingType trainingType);
}
