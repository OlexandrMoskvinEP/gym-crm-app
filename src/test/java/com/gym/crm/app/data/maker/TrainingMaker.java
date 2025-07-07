package com.gym.crm.app.data.maker;

import com.gym.crm.app.domain.model.Training;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.gym.crm.app.data.maker.TraineeMaker.constructTrainee;
import static com.gym.crm.app.data.maker.TrainerMaker.constructTrainer;
import static com.gym.crm.app.data.maker.TrainingTypeMaker.constructTrainingType;

public class TrainingMaker {
    public static Training constructTraining() {
        return Training.builder()
                .trainingName("some awesome training today")
                .trainingType(constructTrainingType())
                .trainingDate(LocalDate.now().minusDays(365))
                .trainingDuration(BigDecimal.valueOf(240))
                .trainer(constructTrainer())
                .trainee(constructTrainee())
                .build();
    }
}
