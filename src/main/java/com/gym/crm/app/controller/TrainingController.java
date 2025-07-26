package com.gym.crm.app.controller;

import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.TrainingCreateRequest;
import com.gym.crm.app.rest.TrainingTypeGetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gym.crm.app.controller.ApiConstants.ROOT_PATH;

@Slf4j
@RestController
@RequestMapping(ROOT_PATH)
public class TrainingController {
    private GymFacade facade;

    @GetMapping("/training-types")
    public ResponseEntity<TrainingTypeGetResponse> getTrainingTypes() {
        log.info("Get training types request");
        TrainingTypeGetResponse response = facade.getAllTrainingsTypes();
        log.info("Training types retrieved, count={}", response.getTrainingTypes() != null ? response.getTrainingTypes().size() : 0);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/trainings")
    public ResponseEntity<Void> addTraining(TrainingCreateRequest trainingCreateRequest) {
        log.info("Add training attempt: traineeUsername={}, trainerUsername={}",
                trainingCreateRequest.getTraineeUsername(),
                trainingCreateRequest.getTrainerUsername());

        facade.addTraining(trainingCreateRequest);

        log.info("Training successfully added for traineeUsername={}, trainerUsername={}",
                trainingCreateRequest.getTraineeUsername(),
                trainingCreateRequest.getTrainerUsername());

        return ResponseEntity.ok().build();
    }
}
