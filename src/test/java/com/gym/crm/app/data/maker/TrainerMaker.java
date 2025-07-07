package com.gym.crm.app.data.maker;

import com.gym.crm.app.domain.model.Trainer;

import static com.gym.crm.app.data.maker.TrainingTypeMaker.constructTrainingType;
import static com.gym.crm.app.data.maker.UserMaker.constructUser;

public class TrainerMaker {
    public static Trainer constructTrainer() {
        return Trainer.builder()
                .specialization(constructTrainingType())
                .build();
    }

    public Trainer constructTrainerWithUserAndTrainingType() {
        return constructTrainer()
                .toBuilder()
                .user(constructUser())
                .build();
    }
}
