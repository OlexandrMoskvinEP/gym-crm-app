package com.gym.crm.app.domain.model;

import lombok.Data;

@Data
public class User {
    private int id;
    private String firstName;
    private String secondName;
    private String username;
    private String password;
    private boolean isActive;
}
