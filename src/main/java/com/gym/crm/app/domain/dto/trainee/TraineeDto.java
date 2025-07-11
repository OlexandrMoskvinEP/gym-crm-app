package com.gym.crm.app.domain.dto.trainee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class TraineeDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
    private LocalDate dateOfBirth;
    private String address;
    private Long userId;
}
