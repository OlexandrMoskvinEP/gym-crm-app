package com.gym.crm.app.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateRequest {
    @Size(min = 3, max = 16, message = "First name shouldn`t be blank and should have 3-16 characters!")
    private String firstName;

    @Size(min = 3, max = 16, message = "First name shouldn`t be blank and should have 3-16 characters!")
    private String lastName;

    @Size(max = 34, message = "Username name shouldn`t be blank or incorrect!")
    private String username;

    @Size(min = 10, max = 64)
    @Pattern(regexp = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*", message = "Password should have at least one special character!")
    private String password;

    @NotBlank(message = "Status can`t be blank!")
    private Boolean isActive;
}
