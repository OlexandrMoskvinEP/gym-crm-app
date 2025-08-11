package com.gym.crm.app.security.service;

import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.model.UserRole;
import com.gym.crm.app.security.model.AuthenticatedUser;

public interface AuthenticatedUserService {
    AuthenticatedUser getAuthenticatedUser(LoginRequest loginRequest);

    UserRole resolveUserRole(String username);
}
