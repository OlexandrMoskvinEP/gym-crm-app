package com.gym.crm.app.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangeActivationStatusDto {
    @NotBlank(message = "Username name should not be blank")
    @Size(min = 1, max = 34, message = "Username name have 1 - 34 characters")
    private String username;

    @NotNull(message = "isActive can`t be null")
    private Boolean isActive;
}
