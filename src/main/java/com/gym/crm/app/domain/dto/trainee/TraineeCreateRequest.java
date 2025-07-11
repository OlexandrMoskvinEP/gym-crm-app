package com.gym.crm.app.domain.dto.trainee;

import com.gym.crm.app.domain.dto.user.UserCreateRequest;
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
public class TraineeCreateRequest {
    @NotNull(message = "User cannot be null")
    private UserCreateRequest user;

    @PastOrPresent(message = "Date of birth should be correct")
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @Size(max = 128)
    @NotBlank(message = "Address cannot exceed 128 characters")
    private String address;
}
