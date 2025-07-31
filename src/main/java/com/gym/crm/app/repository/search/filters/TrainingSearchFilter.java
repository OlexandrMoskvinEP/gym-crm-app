package com.gym.crm.app.repository.search.filters;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder
@Data
@RequiredArgsConstructor
public class TrainingSearchFilter {
    @Size(min = 3, max = 34, message = "Username must be between 1 and 34 characters")
    protected String username;

    protected LocalDate fromDate;
    protected LocalDate toDate;
}
