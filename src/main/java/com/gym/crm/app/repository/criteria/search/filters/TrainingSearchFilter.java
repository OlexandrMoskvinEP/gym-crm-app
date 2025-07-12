package com.gym.crm.app.repository.criteria.search.filters;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder
@Data
public class TrainingSearchFilter {
    @Size(min = 3, max = 34, message = "Username must be between 1 and 34 characters")
    protected String username;

    protected LocalDate fromDate;
    protected LocalDate toDate;
}
