package com.gym.crm.app.repository.search.filters;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TraineeTrainingSearchFilter extends TrainingSearchFilter {
    @Size(min = 3, max = 34, message = "Username must be between 1 and 34 characters")
    private String trainerFullName;

    @Size(max = 32, message = "Training name should not be blank and should have 1 - 32 characters")
    private String trainingTypeName;
}
