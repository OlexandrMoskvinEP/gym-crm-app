package com.gym.crm.app.domain.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class TraineeUpdateRequest {
    @PastOrPresent(message = "Date of birth should be correct!")
    @NotNull(message = "Date of birth is required!")
    private LocalDate dateOfBirth;

    @Size(max = 255)
    @NotBlank(message = "Address cannot exceed 255 characters")
    private String address;
}
