package com.gym.crm.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Training {
    private final int trainerId;
    private final int traineeId;
    private final String trainingName;
    private final TrainingType trainingType;
    private final LocalDate trainingDate;
    private final int trainingDuration;
}
