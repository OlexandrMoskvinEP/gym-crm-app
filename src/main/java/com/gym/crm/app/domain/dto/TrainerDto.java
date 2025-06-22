package com.gym.crm.app.domain.dto;

import com.gym.crm.app.domain.model.TrainingType;
import lombok.Data;

@Data
public class TrainerDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
    private TrainingType specialization;
    private int UserId;
}
