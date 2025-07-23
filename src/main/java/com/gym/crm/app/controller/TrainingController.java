package com.gym.crm.app.controller;

import com.gym.crm.app.api.TrainingTypesApi;
import com.gym.crm.app.api.TrainingsApi;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.TrainingCreateRequest;
import com.gym.crm.app.rest.TrainingTypeGetResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

import static com.gym.crm.app.controller.ApiConstants.ROOT_PATH;

@RestController
@RequestMapping(ROOT_PATH)
public class TrainingController implements TrainingsApi, TrainingTypesApi {
    private GymFacade facade;

    @GetMapping("/training-types")
    public ResponseEntity<TrainingTypeGetResponse> getTrainingTypes() {
        TrainingTypeGetResponse response = facade.getAllTrainingsTypes();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/trainings")
    public ResponseEntity<Void> addTraining(TrainingCreateRequest trainingCreateRequest) {
        facade.addTraining(trainingCreateRequest);

        return ResponseEntity.ok().build();
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return TrainingsApi.super.getRequest();
    }
}
