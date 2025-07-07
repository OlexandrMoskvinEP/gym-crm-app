package com.gym.crm.app.data.maker;

import com.gym.crm.app.domain.model.User;

public class UserMaker {
    public static User constructUser() {
        return User.builder()
                .firstName("Alice")
                .lastName("Moro")
                .username("Alice.Moro")
                .password("Abc123!@#")
                .isActive(true)
                .build();
    }
}
