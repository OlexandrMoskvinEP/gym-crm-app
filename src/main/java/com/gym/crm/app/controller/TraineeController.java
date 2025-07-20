package com.gym.crm.app.controller;

import com.gym.crm.app.api.TraineesApi;
import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.repository.search.filters.TraineeTrainingSearchFilter;
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

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTraineeProfile(@PathVariable String username) {
        facade.deleteTraineeByUsername(username);

        return ResponseEntity.ok().build();
    }

    @GetMapping("{username}/available-trainers")
    public ResponseEntity<AvailableTrainerGetResponse> getAvailableTrainers(@PathVariable String username) {
        AvailableTrainerGetResponse response = facade.getUnassignedTrainersByTraineeUsername(username);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}/trainers")
    public ResponseEntity<TraineeAssignedTrainersUpdateResponse> updateTraineeTrainers(
            TraineeAssignedTrainersUpdateRequest request, @PathVariable String username) {
        TraineeAssignedTrainersUpdateResponse response = facade.updateTraineeTrainersList(username, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<TraineeTrainingGetResponse> getTraineeTrainings(LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType, @PathVariable String username) {
        TraineeTrainingGetResponse response = facade
                .getTraineeTrainingsByFilter(buildTraineeSearchFilter(username, fromDate, toDate, trainerName, trainingType));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{username}/change-activation-status")
    public ResponseEntity<Void> changeTraineeActivationStatus(@PathVariable String username, ActivationStatusRequest
            activationStatusRequest) {

        facade.switchActivationStatus(username);

        return ResponseEntity.ok().build();
    }

    private @Valid TraineeTrainingSearchFilter buildTraineeSearchFilter(String username, LocalDate fromDate,
                                                                        LocalDate toDate, String trainerName, String trainingType) {

        return TraineeTrainingSearchFilter.builder()
                .trainerFullName(trainerName)
                .trainingTypeName(trainingType)
                .fromDate(fromDate)
                .toDate(toDate)
                .username(username).build();
    }
}