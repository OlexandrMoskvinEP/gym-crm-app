package com.gym.crm.app.domain.dto.trainer;

import com.gym.crm.app.domain.dto.user.UserUpdateRequest;
import com.gym.crm.app.domain.model.TrainingType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class TrainerUpdateRequest {
    @NotNull(message = "User cannot be null")
    private UserUpdateRequest user;

    @NotNull(message = "Specialization cannot be null")
    private TrainingType specialization;
}
