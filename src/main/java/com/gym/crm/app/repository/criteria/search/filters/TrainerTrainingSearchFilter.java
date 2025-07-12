package com.gym.crm.app.repository.criteria.search.filters;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TrainerTrainingSearchFilter extends TrainingSearchFilter {
    private String username;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String traineeName;
}
