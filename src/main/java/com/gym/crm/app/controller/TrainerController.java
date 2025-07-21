package com.gym.crm.app.controller;

import com.gym.crm.app.api.TrainersApi;
import com.gym.crm.app.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.ActivationStatusRequest;
import com.gym.crm.app.rest.TrainerCreateResponse;
import com.gym.crm.app.rest.TrainerGetResponse;
import com.gym.crm.app.rest.TrainerTrainingGetResponse;
import com.gym.crm.app.rest.TrainerUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.gym.crm.app.controller.ApiConstants.ROOT_PATH;

@RestController
@RequestMapping(ROOT_PATH + "/trainers")
@RequiredArgsConstructor
public class TrainerController implements TrainersApi {
    private final GymFacade facade;

    @PostMapping("/register")
    public ResponseEntity<TrainerCreateResponse> registerTrainer(@Valid @RequestBody TrainerCreateRequest trainerCreateRequest) {
        TrainerCreateResponse response = facade.addTrainer(trainerCreateRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerGetResponse> getTrainerProfile(@PathVariable("username") String username) {
        TrainerGetResponse response = facade.getTrainerByUsername(username);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerUpdateResponse> updateTrainerProfile(@PathVariable("username") String username,
                                                                      @RequestBody @Valid TrainerUpdateRequest trainerUpdateRequest) {
        TrainerUpdateResponse response = facade.updateTrainerByUsername(username, trainerUpdateRequest);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TrainerTrainingGetResponse> getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        return TrainersApi.super.getTrainerTrainings(username, fromDate, toDate, traineeName);
    }

    @Override
    public ResponseEntity<Void> changeTrainerActivationStatus(String username, ActivationStatusRequest activationStatusRequest) {
        return TrainersApi.super.changeTrainerActivationStatus(username, activationStatusRequest);
    }


}
