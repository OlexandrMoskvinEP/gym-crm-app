package com.gym.crm.app.domain.dto.training;

import com.gym.crm.app.domain.model.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class TrainingResponse {
    private Long trainerId;
    private Long traineeId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private BigDecimal trainingDuration;
}
