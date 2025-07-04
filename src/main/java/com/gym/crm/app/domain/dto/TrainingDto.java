package com.gym.crm.app.domain.dto;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrainingDto {
    private Long trainerId;
    private Long traineeId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private BigDecimal trainingDuration;

    public Trainer getTrainer() {
        return new Trainer();
    }

    public Trainee getTrainee() {
        return new Trainee();
    }
}
