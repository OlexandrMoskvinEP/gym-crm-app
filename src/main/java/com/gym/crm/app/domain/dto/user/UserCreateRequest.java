package com.gym.crm.app.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
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
    @Size(min = 3, max = 16, message = "First name shouldn`t be blank and should have 3-16 characters!")
    private String firstName;

    @Size(min = 3, max = 16, message = "First name shouldn`t be blank and should have 3-16 characters!")
    private String lastName;
}
