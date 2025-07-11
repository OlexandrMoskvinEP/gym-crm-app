package com.gym.crm.app.security;

import com.gym.crm.app.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserHolder {
    private User currentUser;

    public void set(User user) {
        this.currentUser = user;
    }

    public User get() {
        if (currentUser == null) {
            throw new SecurityException("No user is currently authenticated");
        }
        return currentUser;
    }

    public void clear() {
        this.currentUser = null;
    }
}
