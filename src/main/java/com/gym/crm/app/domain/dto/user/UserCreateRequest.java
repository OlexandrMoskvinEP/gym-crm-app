package com.gym.crm.app.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class UserCreateRequest {
    @NotBlank(message = "First name should not be blank")
    @Size(min = 3, max = 16, message = "First name should not be blank and should have 3 - 16 characters")
    private String firstName;

    @NotBlank(message = "Last name should not be blank")
    @Size(min = 3, max = 16, message = "Last name should have 3 - 16 characters")
    private String lastName;

    @NotNull(message = "isActive can`t be null")
    private Boolean isActive;
}
