package com.gym.crm.app.domain.dto.training;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TrainingCreateRequest {
    @Size(min = 3, max = 64, message = "Training name shouldn`t be blank and should have 3-16 characters!")
    private String trainingName;

    @NotNull
    @FutureOrPresent(message = "Date should be correct!")
    private LocalDate trainingDate;

    @NotNull
    @Positive(message = "Training duration should be greater than 0!")
    private BigDecimal trainingDuration;

    @NotNull(message = "Trainee id shouldn`t be null!")
    private Long traineeId;

    @NotNull(message = "Trainer id shouldn`t be null!")
    private Long trainerId;

    @NotNull(message = "Training type id shouldn`t be null!")
    private Long trainingTypeId;
}
