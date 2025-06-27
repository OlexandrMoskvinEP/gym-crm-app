package com.gym.crm.app.facade;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GymFacade {
    private final TrainerFacade trainerFacade;
    private final TraineeFacade traineeFacade;
    private final TrainingFacade trainingFacade;

    public GymFacade(TrainerFacade trainerFacade, TraineeFacade traineeFacade, TrainingFacade trainingFacade) {
        this.trainerFacade = trainerFacade;
        this.traineeFacade = traineeFacade;
        this.trainingFacade = trainingFacade;
    }
}
