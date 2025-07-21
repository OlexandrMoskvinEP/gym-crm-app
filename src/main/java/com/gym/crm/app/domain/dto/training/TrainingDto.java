package com.gym.crm.app.domain.dto.training;

import com.gym.crm.app.domain.model.TrainingType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class TrainingDto {
    private Long trainerId;
    private Long traineeId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private BigDecimal trainingDuration;
}
