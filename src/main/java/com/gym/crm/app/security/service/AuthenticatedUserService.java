package com.gym.crm.app.security.service;

import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.model.AuthenticatedUser;
import com.gym.crm.app.security.model.UserRole;

public interface AuthenticatedUserService {
    AuthenticatedUser getAuthenticatedUser(LoginRequest loginRequest);

    UserRole resolveUserRole(String username);
}
