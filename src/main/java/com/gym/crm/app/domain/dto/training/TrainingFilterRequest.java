package com.gym.crm.app.domain.dto.training;

import com.gym.crm.app.domain.model.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TrainingFilterRequest {
    private String username;
    private boolean isTraineeQuery;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String partnerName;
    private TrainingType trainingType;
}
