package com.gym.crm.app.security;

import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.security.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserHolder {
    private static final ThreadLocal<AuthenticatedUser> CURRENT_USER = new ThreadLocal<>();

    public void set(AuthenticatedUser user) {
        CURRENT_USER.set(user);
    }

    public AuthenticatedUser get() {
        AuthenticatedUser user = CURRENT_USER.get();

        if (user == null) {
            throw new AuthentificationErrorException("No user is currently authenticated");
        }
        return user;
    }

    public void clear() {
        CURRENT_USER.remove();
    }
}
