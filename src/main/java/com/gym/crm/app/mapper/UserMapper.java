package com.gym.crm.app.mapper;

import com.gym.crm.app.domain.dto.user.UserCreateRequest;
import com.gym.crm.app.security.UserRole;
import com.gym.crm.app.security.model.UserCredentialsDto;
import com.gym.crm.app.domain.dto.user.UserUpdateRequest;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.security.model.AuthenticatedUser;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toEntity(UserCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User update(@MappingTarget User user, UserUpdateRequest dto);

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    UserCredentialsDto toCredentialsDto(AuthenticatedUser user);

    @Mapping(source = "active", target = "isActive")
    AuthenticatedUser toAuthenticatedUser(User user);

    @Named("roleToString")
    static String roleToString(UserRole role) {
        return role.name();
    }
}
