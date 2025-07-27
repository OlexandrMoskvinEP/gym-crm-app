package com.gym.crm.app.controller;

import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.ErrorResponse;
import com.gym.crm.app.rest.TrainingCreateRequest;
import com.gym.crm.app.rest.TrainingTypeGetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private GymFacade facade;

    @Operation(
            summary = "Get training types",
            description = "Retrieves list of all available training types",
            tags = {"Training services"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of training types retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    description = "Unexpected error"
            )
    })
    @GetMapping("/training-types")
    public ResponseEntity<TrainingTypeGetResponse> getTrainingTypes() {
        log.info("Get training types request");
        TrainingTypeGetResponse response = facade.getAllTrainingsTypes();
        log.info("Training types retrieved, count={}", response.getTrainingTypes() != null ? response.getTrainingTypes().size() : 0);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Add new training",
            description = "Creates a new training session between trainee and trainer",
            tags = {"Training services"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Training added successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ), @ApiResponse(
            responseCode = "401",
            description = "Unauthorised access",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ), @ApiResponse(
            responseCode = "404",
            description = "Trainee or trainer not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    description = "Unexpected error"
            )
    })
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
