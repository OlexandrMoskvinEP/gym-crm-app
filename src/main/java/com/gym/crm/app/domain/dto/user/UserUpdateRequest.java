package com.gym.crm.app.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)

@Data
public class UserUpdateRequest {
    private static final String VALID_CHAR_PATTERN = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";

    @NotBlank(message = "First name should not be blank")
    @Size(min = 3, max = 16, message = "First name should not be blank and should have 3 - 16 characters")
    private String firstName;

    @NotBlank(message = "Last name should not be blank")
    @Size(min = 3, max = 16, message = "Last name should have 3 - 16 characters")
    private String lastName;

    @NotBlank(message = "Username name should not be blank")
    @Size(min = 1, max = 34, message = "Username name have 1 - 34 characters")
    private String username;

    @Size(min = 10, max = 20)
    @Pattern(regexp = VALID_CHAR_PATTERN, message = "Password should have at least one special character")
    private String password;

    @NotNull(message = "isActive can`t be null")
    private Boolean isActive;
}
