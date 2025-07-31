package com.gym.crm.app.repository.search.filters;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class TrainerTrainingSearchFilter extends TrainingSearchFilter {
    @Size(min = 3, max = 34, message = "Username must be between 1 and 34 characters")
    private String traineeFullName;
}
