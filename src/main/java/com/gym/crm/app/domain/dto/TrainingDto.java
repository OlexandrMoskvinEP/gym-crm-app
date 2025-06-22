package com.gym.crm.app.domain.dto;

import com.gym.crm.app.domain.model.TrainingType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrainingDto {
    private int trainerId;
    private int traineeId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDateTime trainingDate;
    private int trainingDuration;
}
