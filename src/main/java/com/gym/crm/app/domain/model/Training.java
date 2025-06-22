package com.gym.crm.app.domain.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Training {
    private int trainerId;
    private int traineeId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private int trainingDuration;
}
