package com.gym.crm.app.mapper;

import com.gym.crm.app.domain.dto.user.UserCreateRequest;
import com.gym.crm.app.domain.dto.user.UserCredentialsDto;
import com.gym.crm.app.domain.dto.user.UserUpdateRequest;
import com.gym.crm.app.domain.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toEntity(UserCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User update(@MappingTarget User user, UserUpdateRequest dto);

    UserCredentialsDto toCredentialsDto(User user);
}
