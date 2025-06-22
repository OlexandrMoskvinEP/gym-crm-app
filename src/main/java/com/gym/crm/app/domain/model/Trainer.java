package com.gym.crm.app.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Trainer extends User{
    private TrainingType specialisation;
}
