package com.gym.crm.app.mapper;

import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.rest.TraineeCreateResponse;
import com.gym.crm.app.rest.TraineeGetResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TraineeMapper {
    Trainee toEntity(TraineeCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Trainee trainee, TraineeUpdateRequest dto);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.password", target = "password")
    @Mapping(source = "user.active", target = "isActive")
    @Mapping(source = "user.id", target = "userId")
    TraineeDto toDto(Trainee trainee);

    TraineeCreateResponse dtoToResponse(TraineeDto traineeDto);

    @Mapping(source = "active", target = "isActive")
    @Mapping(target = "trainers", ignore = true)
    TraineeGetResponse dtoToGetResponse(TraineeDto dto);
}
