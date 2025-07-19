package com.gym.crm.app.mapper;

import com.gym.crm.app.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.rest.AvailableTrainerGetResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TrainerMapper {
    Trainer toEntity(TrainerCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Trainer trainer, TrainerUpdateRequest dto);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.password", target = "password")
    @Mapping(source = "user.active", target = "isActive")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "specialization", target = "specialization")
    TrainerDto toDto(Trainer trainer);

    @Mapping(source = "specialization.trainingTypeName", target = "specialization")
    AvailableTrainerGetResponse dtoToAvailableTrainerResponse(TrainerDto trainerDto);
}
