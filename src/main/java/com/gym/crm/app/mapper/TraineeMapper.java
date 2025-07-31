package com.gym.crm.app.mapper;

import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.rest.TraineeCreateResponse;
import com.gym.crm.app.rest.TraineeGetResponse;
import com.gym.crm.app.rest.TraineeUpdateResponse;
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
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "id", target = "traineeId")
    TraineeDto toDto(Trainee trainee);

    TraineeCreateResponse dtoToCreateResponse(TraineeDto traineeDto);

    @Mapping(target = "trainer", ignore = true)
    @Mapping(source = "active", target = "isActive")
    TraineeGetResponse dtoToGetResponse(TraineeDto dto);

    @Mapping(target = "trainers", ignore = true)
    @Mapping(source = "active", target = "isActive")
    TraineeUpdateResponse dtoToUpdateResponse(TraineeDto traineeDto);
}
