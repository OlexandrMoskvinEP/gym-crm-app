package com.gym.crm.app.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class Trainee extends User {
    private LocalDate dateOfBirth;
    private String address;
    private int userId;
}
