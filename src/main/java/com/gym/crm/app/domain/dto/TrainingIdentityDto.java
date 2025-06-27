package com.gym.crm.app.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingIdentityDto {
    private int traineeId;
    private int trainerId;
    private LocalDate trainingDate;
}
