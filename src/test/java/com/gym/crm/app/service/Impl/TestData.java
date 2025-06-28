package com.gym.crm.app.service.Impl;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
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
}
