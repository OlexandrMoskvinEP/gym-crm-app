package com.gym.crm.app.domain.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TraineeDto {
    private String firstName;
    private String secondName;
    private String username;
    private String password;
    private boolean isActive;
    private LocalDate dateOfBirth;
    private String address;

}
