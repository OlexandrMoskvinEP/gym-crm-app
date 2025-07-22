package com.gym.crm.app.security;

import com.gym.crm.app.security.model.AuthenticatedUser;
import com.gym.crm.app.exception.AuthentificationException;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserHolder {
    private AuthenticatedUser currentUser;

    public void set(AuthenticatedUser user) {
        this.currentUser = user;
    }

    public AuthenticatedUser get() {
        if (currentUser == null) {
            throw new AuthentificationException("No user is currently authenticated");
        }
        return currentUser;
    }

    public void clear() {
        this.currentUser = null;
    }
}
