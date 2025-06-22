package com.gym.crm.app.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Training {
    private int trainerId;
    private int traineeId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDateTime trainingDate;
    private int trainingDuration;
}
