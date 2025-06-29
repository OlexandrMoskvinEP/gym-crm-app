package com.gym.crm.app.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class User {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    @JsonProperty("isActive")
    private final boolean isActive;

    @JsonProperty("isActive")
    public boolean getIsActive() {
        return isActive;
    }
}