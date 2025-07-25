package com.gym.crm.app.security.model;

import com.gym.crm.app.security.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class UserCredentialsDto {
    private String username;
    private String password;
    private String role;
}
