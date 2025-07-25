package com.gym.crm.app.mapper;

import com.gym.crm.app.exception.handling.ErrorCode;
import com.gym.crm.app.rest.ErrorResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ErrorResponseMapper {
    ErrorResponse toErrorResponse(ErrorCode errorCode);
}
