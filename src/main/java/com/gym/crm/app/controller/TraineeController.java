package com.gym.crm.app.controller;

import com.gym.crm.app.api.TraineesApi;
import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.gym.crm.app.controller.ApiConstants.ROOT_PATH;

@RestController
@RequestMapping(ROOT_PATH + "/trainees")
@RequiredArgsConstructor
public class TraineeController implements TraineesApi {
    private final GymFacade facade;

    @PostMapping("/register")
    public ResponseEntity<TraineeCreateResponse> registerTrainee(@Valid @RequestBody TraineeCreateRequest traineeCreateRequest) {
        TraineeCreateResponse response = facade.addTrainee(traineeCreateRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeGetResponse> getTraineeProfile(@PathVariable String username) {
        TraineeGetResponse response = facade.getTraineeByUsername(username);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeUpdateResponse> updateTraineeProfile(@PathVariable String username, TraineeUpdateRequest traineeUpdateRequest) {
        TraineeUpdateResponse response = facade.updateTraineeByUsername(username, traineeUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteTraineeProfile(String username) {
        return TraineesApi.super.deleteTraineeProfile(username);
    }

    @Override
    public ResponseEntity<List<AvailableTrainerGetResponse>> getAvailableTrainers(String username) {
        return TraineesApi.super.getAvailableTrainers(username);
    }

    @Override
    public ResponseEntity<List<TraineeTrainingGetResponse>> getTraineeTrainings(String username, LocalDate
            fromDate, LocalDate toDate, String trainerName, String trainingType) {
        return TraineesApi.super.getTraineeTrainings(username, fromDate, toDate, trainerName, trainingType);
    }

    @Override
    public ResponseEntity<Void> changeTraineeActivationStatus(String username, ActivationStatusRequest
            activationStatusRequest) {
        return TraineesApi.super.changeTraineeActivationStatus(username, activationStatusRequest);
    }

    @Override
    public ResponseEntity<List<TraineeAssignedTrainersUpdateResponse>> updateTraineeTrainers(String
                                                                                                     username, TraineeAssignedTrainersUpdateRequest traineeAssignedTrainersUpdateRequest) {
        return TraineesApi.super.updateTraineeTrainers(username, traineeAssignedTrainersUpdateRequest);
    }
}
