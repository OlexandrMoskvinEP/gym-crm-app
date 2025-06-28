package com.gym.crm.app.service.Impl;

import com.gym.crm.app.domain.dto.TrainingIdentityDto;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.TrainingType;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class TestData {

    List<Trainee> trainees = List.of(
            Trainee.builder().firstName("Alice").lastName("Smith").username("Alice.Smith").password("Abc123!@#")
                    .dateOfBirth(LocalDate.of(1990, 3, 12)).address("Main Street").userId(121).isActive(true).build(),
            Trainee.builder().firstName("Bob").lastName("Williams").username("Bob.Williams").password("Def456$%^")
                    .dateOfBirth(LocalDate.of(1992, 6, 23)).address("Broadway").userId(122).isActive(true).build(),
            Trainee.builder().firstName("Carol").lastName("Johnson").username("Carol.Johnson").password("Ytr789#Rr")
                    .dateOfBirth(LocalDate.of(1988, 8, 15)).address("2nd Avenue").userId(123).isActive(false).build(),
            Trainee.builder().firstName("David").lastName("Brown").username("David.Brown").password("Ghi789&*(")
                    .dateOfBirth(LocalDate.of(1985, 11, 5)).address("Elm Street").userId(124).isActive(true).build(),
            Trainee.builder().firstName("Eva").lastName("Davis").username("Eva.Davis").password("Jkl321)(*")
                    .dateOfBirth(LocalDate.of(1995, 1, 30)).address("Pine Avenue").userId(125).isActive(false).build());

    List<Trainer> trainers = List.of(
            Trainer.builder().firstName("Sophie").lastName("Taylor").username("Sophie.Taylor").password("S0ph!e456")
                    .specialization(new TrainingType("aerobics")).userId(204).isActive(true).build(),
            Trainer.builder().firstName("Liam").lastName("Johnson").username("Liam.Johnson").password("L1am!789")
                    .specialization(new TrainingType("crossfit")).userId(205).isActive(true).build(),
            Trainer.builder().firstName("Olivia").lastName("Brown").username("Olivia.Brown").password("Ol!via123")
                    .specialization(new TrainingType("yoga")).userId(206).isActive(false).build(),
            Trainer.builder().firstName("James").lastName("Wilson").username("James.Wilson").password("J@mes456!")
                    .specialization(new TrainingType("boxing")).userId(207).isActive(true).build(),
            Trainer.builder().firstName("Emily").lastName("Clark").username("Emily.Clark").password("Em!ly789#")
                    .specialization(new TrainingType("pilates")).userId(208).isActive(false).build()
    );

    List<Training> trainings = List.of(
            Training.builder().trainerId(321).traineeId(654).trainingName("Pokatushka")
                    .trainingType(new TrainingType("Cycling"))
                    .trainingDate(LocalDate.of(2025, 6, 28)).trainingDuration(120)
                    .build(),
            Training.builder().trainerId(204).traineeId(121).trainingName("Morning Yoga")
                    .trainingType(new TrainingType("Yoga"))
                    .trainingDate(LocalDate.of(2025, 6, 27)).trainingDuration(60)
                    .build(),
            Training.builder().trainerId(205).traineeId(122).trainingName("CrossFit Blast")
                    .trainingType(new TrainingType("CrossFit"))
                    .trainingDate(LocalDate.of(2025, 6, 26)).trainingDuration(75)
                    .build(),
            Training.builder().trainerId(206).traineeId(123).trainingName("Pilates Session")
                    .trainingType(new TrainingType("Pilates"))
                    .trainingDate(LocalDate.of(2025, 6, 25)).trainingDuration(50)
                    .build(),
            Training.builder().trainerId(207).traineeId(124).trainingName("Boxing Drills")
                    .trainingType(new TrainingType("Boxing"))
                    .trainingDate(LocalDate.of(2025, 6, 24)).trainingDuration(90)
                    .build()
    );

    List<TrainingIdentityDto> identities = List.of(
            new TrainingIdentityDto(654, 321, LocalDate.of(2025, 6, 28)),
            new TrainingIdentityDto(121, 204, LocalDate.of(2025, 6, 27)),
            new TrainingIdentityDto(122, 205, LocalDate.of(2025, 6, 26)),
            new TrainingIdentityDto(123, 206, LocalDate.of(2025, 6, 25)),
            new TrainingIdentityDto(124, 207, LocalDate.of(2025, 6, 24))
    );

}
