package com.gym.crm.app.controller;

import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.repository.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.rest.ActivationStatusRequest;
import com.gym.crm.app.rest.AvailableTrainerGetResponse;
import com.gym.crm.app.rest.TraineeAssignedTrainersUpdateRequest;
import com.gym.crm.app.rest.TraineeAssignedTrainersUpdateResponse;
import com.gym.crm.app.rest.TraineeCreateResponse;
import com.gym.crm.app.rest.TraineeGetResponse;
import com.gym.crm.app.rest.TraineeTrainingGetResponse;
import com.gym.crm.app.rest.TraineeUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

@Slf4j
@RestController
@RequestMapping(ROOT_PATH + "/trainees")
@RequiredArgsConstructor
public class TraineeController {
    private final GymFacade facade;

    @PostMapping("/register")
    public ResponseEntity<TraineeCreateResponse> registerTrainee(@Valid @RequestBody TraineeCreateRequest traineeCreateRequest) {
        log.info("Trainee registration attempt: firstName={} lastName={}",
                traineeCreateRequest.getUser().getFirstName(),
                traineeCreateRequest.getUser().getLastName());
        TraineeCreateResponse response = facade.addTrainee(traineeCreateRequest);
        log.info("Trainee registered successfully: username={}", response.getUsername());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeGetResponse> getTraineeProfile(@PathVariable("username") String username) {
        log.info("Trainee profile request: username={}", username);
        TraineeGetResponse response = facade.getTraineeByUsername(username);
        log.info("Trainee profile retrieved: username={}", username);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeUpdateResponse> updateTraineeProfile(@PathVariable("username") String username, @Valid @RequestBody TraineeUpdateRequest traineeUpdateRequest) {
        log.info("Update trainee profile attempt: username={}", username);
        TraineeUpdateResponse response = facade.updateTraineeByUsername(username, traineeUpdateRequest);
        log.info("Trainee profile updated: username={}", username);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTraineeProfile(@PathVariable("username") String username) {
        log.info("Delete trainee profile attempt: username={}", username);
        facade.deleteTraineeByUsername(username);
        log.info("Trainee profile deleted: username={}", username);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("{username}/available-trainers")
    public ResponseEntity<AvailableTrainerGetResponse> getAvailableTrainers(@PathVariable("username") String username) {
        log.info("Get available trainers for trainee: username={}", username);
        AvailableTrainerGetResponse response = facade.getUnassignedTrainersByTraineeUsername(username);
        log.info("Available trainers retrieved for trainee: username={}", username);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}/trainers")
    public ResponseEntity<TraineeAssignedTrainersUpdateResponse> updateTraineeTrainers(
            @Valid @RequestBody TraineeAssignedTrainersUpdateRequest request, @PathVariable("username") String username) {
        log.info("Update trainee trainers attempt: username={}", username);
        TraineeAssignedTrainersUpdateResponse response = facade.updateTraineeTrainersList(username, request);
        log.info("Trainee trainers updated: username={}", username);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<TraineeTrainingGetResponse> getTraineeTrainings(@RequestParam(name = "fromDate", required = false) LocalDate fromDate,
                                                                          @RequestParam(name = "toDate", required = false) LocalDate toDate,
                                                                          @RequestParam(name = "trainerName", required = false) String trainerName,
                                                                          @RequestParam(name = "trainingType", required = false) String trainingType,
                                                                          @PathVariable("username") String username) {
        log.info("Get trainee trainings request: username={}, fromDate={}, toDate={}, trainerName={}, trainingType={}",
                username, fromDate, toDate, trainerName, trainingType);
        TraineeTrainingGetResponse response = facade
                .getTraineeTrainingsByFilter(buildTraineeSearchFilter(username, fromDate, toDate, trainerName, trainingType));
        log.info("Trainee trainings retrieved: username={}", username);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{username}/change-activation-status")
    public ResponseEntity<Void> changeTraineeActivationStatus(@PathVariable("username") String username,
                                                              @RequestBody ActivationStatusRequest activationStatusRequest) {
        log.info("Change trainee activation status attempt: username={}, newStatus={}", username, activationStatusRequest.getIsActive());
        facade.switchActivationStatus(username, activationStatusRequest);
        log.info("Trainee activation status changed: username={}", username);

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