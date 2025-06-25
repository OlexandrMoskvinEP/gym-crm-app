package com.gym.crm.app.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean active;
}