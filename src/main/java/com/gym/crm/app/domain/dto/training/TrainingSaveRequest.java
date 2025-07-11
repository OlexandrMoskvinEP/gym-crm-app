package com.gym.crm.app.domain.dto.training;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class TrainingSaveRequest {
    @Size(min = 3, max = 32, message = "Training name should not be blank and should have 3 - 32 characters")
    private String trainingName;

    @NotNull
    @FutureOrPresent(message = "Date should be correct")
    private LocalDate trainingDate;

    @NotNull
    @Positive(message = "Training duration should be greater than 0")
    private BigDecimal trainingDuration;

    @NotNull(message = "Trainee id should not be null")
    private Long traineeId;

    @NotNull(message = "Trainer id should not be null")
    private Long trainerId;

    @NotBlank(message = "Training type name should not be null or empty")
    @Size(max = 32, message = "Training name should not be blank and should have 1 - 32 characters")
    private String trainingTypeName;
}
