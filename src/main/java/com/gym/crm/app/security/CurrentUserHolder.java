package com.gym.crm.app.security;

import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.security.model.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserHolder {
    private static final String SESSION_USER_KEY = "AUTHENTICATED_USER";
    private static final int AUTH_EXPIRATION_TIME = 3600;

    private final HttpServletRequest request;

    public void set(AuthenticatedUser user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_USER_KEY, user);
        session.setMaxInactiveInterval(AUTH_EXPIRATION_TIME);
    }

    public AuthenticatedUser get() {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SESSION_USER_KEY) == null) {
            throw new AuthentificationErrorException("No user is currently authenticated");
        }
        return (AuthenticatedUser) session.getAttribute(SESSION_USER_KEY);
    }

    public void clear() {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_USER_KEY);
        }
    }
}