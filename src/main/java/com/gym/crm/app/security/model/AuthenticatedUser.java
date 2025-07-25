package com.gym.crm.app.security.model;

import com.gym.crm.app.security.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class AuthenticatedUser{
    private final Long userId;
    private final String username;
    private final String password;
    private final UserRole role;
    private final boolean isActive;
}
