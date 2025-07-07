package com.gym.crm.app.data.maker;

import com.gym.crm.app.domain.model.TrainingType;

public class TrainingTypeMaker {
    public static TrainingType constructTrainingType(){
        return TrainingType.builder()
                .trainingTypeName("some awesome training")
                .build();
    }
}
