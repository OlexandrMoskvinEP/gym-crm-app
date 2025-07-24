package com.gym.crm.app.controller;

import com.gym.crm.app.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.repository.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.app.rest.ActivationStatusRequest;
import com.gym.crm.app.rest.TrainerCreateResponse;
import com.gym.crm.app.rest.TrainerGetResponse;
import com.gym.crm.app.rest.TrainerTrainingGetResponse;
import com.gym.crm.app.rest.TrainerUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.gym.crm.app.controller.ApiConstants.ROOT_PATH;

@RestController
@RequestMapping(ROOT_PATH + "/trainers")
@RequiredArgsConstructor
public class TrainerController {
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

    @GetMapping("/{username}/trainings")
    public ResponseEntity<TrainerTrainingGetResponse> getTrainerTrainings(@PathVariable("username") String username,
                                                                          @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
                                                                          @RequestParam(name = "toDate", required = false) LocalDate toDate,
                                                                          @RequestParam(name = "traineeName", required = false) String traineeName) {
        TrainerTrainingSearchFilter filter = buildTrainerTrainingSearchFilter(fromDate, toDate, traineeName, username);

        TrainerTrainingGetResponse response = facade.getTrainerTrainingsByFilter(filter);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{username}/change-activation-status")
    public ResponseEntity<Void> changeTrainerActivationStatus(@PathVariable("username") String username,
                                                              @RequestBody ActivationStatusRequest activationStatusRequest) {
        facade.switchActivationStatus(username);

        return ResponseEntity.ok().build();
    }

    private @Valid TrainerTrainingSearchFilter buildTrainerTrainingSearchFilter(LocalDate fromDate, LocalDate toDate, String traineeName, String username) {
        return TrainerTrainingSearchFilter.builder()
                .username(username)
                .traineeFullName(traineeName)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
    }
}
