package com.gym.crm.app.data;

import com.gym.crm.app.domain.dto.TrainingIdentityDto;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.domain.model.User;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
public class TestData {
    List<Trainee> trainees = List.of(
            Trainee.builder()
                    .dateOfBirth(LocalDate.of(1990, 3, 12))
                    .address("Main Street")
                    .user(User.builder()
                            .firstName("Alice")
                            .lastName("Smith")
                            .username("Alice.Smith")
                            .password("Abc123!@#")
                            .isActive(true)
                            .build())
                    .build(),
            Trainee.builder()
                    .dateOfBirth(LocalDate.of(1992, 6, 23))
                    .address("Broadway")
                    .user(User.builder()
                            .firstName("Bob")
                            .lastName("Williams")
                            .username("Bob.Williams")
                            .password("Def456$%^")
                            .isActive(true)
                            .build())
                    .build(),
            Trainee.builder()
                    .dateOfBirth(LocalDate.of(1988, 8, 15))
                    .address("2nd Avenue")
                    .user(User.builder()
                            .firstName("Carol")
                            .lastName("Johnson")
                            .username("Carol.Johnson")
                            .password("Ytr789#Rr")
                            .isActive(false)
                            .build())
                    .build(),
            Trainee.builder()
                    .dateOfBirth(LocalDate.of(1985, 11, 5))
                    .address("Elm Street")
                    .user(User.builder()
                            .firstName("David")
                            .lastName("Brown")
                            .username("David.Brown")
                            .password("Ghi789&*(")
                            .isActive(true)
                            .build())
                    .build(),
            Trainee.builder()
                    .dateOfBirth(LocalDate.of(1995, 1, 30))
                    .address("Pine Avenue")
                    .user(User.builder()
                            .firstName("Eva")
                            .lastName("Davis")
                            .username("Eva.Davis")
                            .password("Jkl321)(*")
                            .isActive(false)
                            .build())
                    .build()
    );

    List<Trainer> trainers = List.of(
            Trainer.builder()
                    .specialization(new TrainingType(1l, "aerobics"))
                    .user(User.builder()
                            .firstName("Sophie")
                            .lastName("Taylor")
                            .username("Sophie.Taylor")
                            .password("S0ph!e456")
                            .isActive(true)
                            .build())
                    .build(),
            Trainer.builder()
                    .specialization(new TrainingType(2l, "crossfit"))
                    .user(User.builder()
                            .firstName("Liam")
                            .lastName("Johnson")
                            .username("Liam.Johnson")
                            .password("L1am!789")
                            .isActive(true)
                            .build())
                    .build(),
            Trainer.builder()
                    .specialization(new TrainingType(3l, "yoga"))
                    .user(User.builder()
                            .firstName("Olivia")
                            .lastName("Brown")
                            .username("Olivia.Brown")
                            .password("Ol!via123")
                            .isActive(false)
                            .build())
                    .build(),
            Trainer.builder()
                    .specialization(new TrainingType(4l, "boxing"))
                    .user(User.builder()
                            .firstName("James")
                            .lastName("Wilson")
                            .username("James.Wilson")
                            .password("J@mes456!")
                            .isActive(true)
                            .build())
                    .build(),
            Trainer.builder()
                    .specialization(new TrainingType(5l, "pilates"))
                    .user(User.builder()
                            .firstName("Emily")
                            .lastName("Clark")
                            .username("Emily.Clark")
                            .password("Em!ly789#")
                            .isActive(false)
                            .build())
                    .build()
    );


    List<Training> trainings = List.of(
            Training.builder()
                    .id(1L)
                    .trainingName("Pokatushka")
                    .trainer(Trainer.builder().id(321L).build())
                    .trainee(Trainee.builder().id(654L).build())
                    .trainingType(new TrainingType(1L, "Cycling"))
                    .trainingDate(LocalDate.of(2025, 6, 28))
                    .trainingDuration(BigDecimal.valueOf(120))
                    .build(),
            Training.builder()
                    .id(2L)
                    .trainingName("Morning Yoga")
                    .trainer(Trainer.builder().id(204L).build())
                    .trainee(Trainee.builder().id(121L).build())
                    .trainingType(new TrainingType(2L, "Yoga"))
                    .trainingDate(LocalDate.of(2025, 6, 27))
                    .trainingDuration(BigDecimal.valueOf(60))
                    .build(),
            Training.builder()
                    .id(3L)
                    .trainingName("CrossFit Blast")
                    .trainer(Trainer.builder().id(205L).build())
                    .trainee(Trainee.builder().id(122L).build())
                    .trainingType(new TrainingType(3L, "CrossFit"))
                    .trainingDate(LocalDate.of(2025, 6, 26))
                    .trainingDuration(BigDecimal.valueOf(75))
                    .build(),
            Training.builder()
                    .id(4L)
                    .trainingName("Pilates Session")
                    .trainer(Trainer.builder().id(206L).build())
                    .trainee(Trainee.builder().id(123L).build())
                    .trainingType(new TrainingType(4L, "Pilates"))
                    .trainingDate(LocalDate.of(2025, 6, 25))
                    .trainingDuration(BigDecimal.valueOf(50))
                    .build(),
            Training.builder()
                    .id(5L)
                    .trainingName("Boxing Drills")
                    .trainer(Trainer.builder().id(207L).build())
                    .trainee(Trainee.builder().id(124L).build())
                    .trainingType(new TrainingType(5L, "Boxing"))
                    .trainingDate(LocalDate.of(2025, 6, 24))
                    .trainingDuration(BigDecimal.valueOf(90))
                    .build()
    );

    List<TrainingIdentityDto> identities = List.of(
            new TrainingIdentityDto(321L, 654L, LocalDate.of(2025, 6, 28)),
            new TrainingIdentityDto(204L, 121L, LocalDate.of(2025, 6, 27)),
            new TrainingIdentityDto(205L, 122L, LocalDate.of(2025, 6, 26)),
            new TrainingIdentityDto(206L, 123L, LocalDate.of(2025, 6, 25)),
            new TrainingIdentityDto(207L, 124L, LocalDate.of(2025, 6, 24))
    );

    Map<String, Trainer> TRAINER_STORAGE = Map.of(
            "Sophie.Taylor", Trainer.builder()
                    .specialization(new TrainingType(1l, "aerobics"))
                    .user(User.builder()
                            .firstName("Sophie")
                            .lastName("Taylor")
                            .username("Sophie.Taylor")
                            .password("S0ph!e456")
                            .isActive(true)
                            .build())
                    .build(),
            "Liam.Johnson", Trainer.builder()
                    .specialization(new TrainingType(2l, "crossfit"))
                    .user(User.builder()
                            .firstName("Liam")
                            .lastName("Johnson")
                            .username("Liam.Johnson")
                            .password("L1am!789")
                            .isActive(true)
                            .build())
                    .build(),
            "Olivia.Brown", Trainer.builder()
                    .specialization(new TrainingType(3l, "yoga"))
                    .user(User.builder()
                            .firstName("Olivia")
                            .lastName("Brown")
                            .username("Olivia.Brown")
                            .password("Ol!via123")
                            .isActive(false)
                            .build())
                    .build(),
            "James.Wilson", Trainer.builder()
                    .specialization(new TrainingType(4l, "boxing"))
                    .user(User.builder()
                            .firstName("James")
                            .lastName("Wilson")
                            .username("James.Wilson")
                            .password("J@mes456!")
                            .isActive(true)
                            .build())
                    .build(),
            "Emily.Clark", Trainer.builder()
                    .specialization(new TrainingType(5l, "pilates"))
                    .user(User.builder()
                            .firstName("Emily")
                            .lastName("Clark")
                            .username("Emily.Clark")
                            .password("Em!ly789#")
                            .isActive(false)
                            .build())
                    .build()
    );


    Map<String, Trainee> TRAINEE_STORAGE = Map.of(
            "Alice.Smith", Trainee.builder()
                    .dateOfBirth(LocalDate.of(1990, 3, 12))
                    .address("Main Street")
                    .user(User.builder()
                            .firstName("Alice")
                            .lastName("Smith")
                            .username("Alice.Smith")
                            .password("Abc123!@#")
                            .isActive(true)
                            .build())
                    .build(),
            "Bob.Williams", Trainee.builder()
                    .dateOfBirth(LocalDate.of(1992, 6, 23))
                    .address("Broadway")
                    .user(User.builder()
                            .firstName("Bob")
                            .lastName("Williams")
                            .username("Bob.Williams")
                            .password("Def456$%^")
                            .isActive(true)
                            .build())
                    .build(),
            "Carol.Johnson", Trainee.builder()
                    .dateOfBirth(LocalDate.of(1988, 8, 15))
                    .address("2nd Avenue")
                    .user(User.builder()
                            .firstName("Carol")
                            .lastName("Johnson")
                            .username("Carol.Johnson")
                            .password("Ytr789#Rr")
                            .isActive(false)
                            .build())
                    .build(),
            "David.Brown", Trainee.builder()
                    .dateOfBirth(LocalDate.of(1985, 11, 5))
                    .address("Elm Street")
                    .user(User.builder()
                            .firstName("David")
                            .lastName("Brown")
                            .username("David.Brown")
                            .password("Ghi789&*(")
                            .isActive(true)
                            .build())
                    .build(),
            "Eva.Davis", Trainee.builder()
                    .dateOfBirth(LocalDate.of(1995, 1, 30))
                    .address("Pine Avenue")
                    .user(User.builder()
                            .firstName("Eva")
                            .lastName("Davis")
                            .username("Eva.Davis")
                            .password("Jkl321)(*")
                            .isActive(false)
                            .build())
                    .build()
    );


    Map<String, Training> TRAINING_STORAGE = Map.of(
            "321_654_2025-06-28", Training.builder()
                    .id(1L)
                    .trainer(Trainer.builder().id(321L).build())
                    .trainee(Trainee.builder().id(654L).build())
                    .trainingName("Pokatushka")
                    .trainingType(new TrainingType(1L, "Cycling"))
                    .trainingDate(LocalDate.of(2025, 6, 28))
                    .trainingDuration(BigDecimal.valueOf(120))
                    .build(),
            "204_121_2025-06-27", Training.builder()
                    .id(2L)
                    .trainer(Trainer.builder().id(204L).build())
                    .trainee(Trainee.builder().id(121L).build())
                    .trainingName("Morning Yoga")
                    .trainingType(new TrainingType(2L, "Yoga"))
                    .trainingDate(LocalDate.of(2025, 6, 27))
                    .trainingDuration(BigDecimal.valueOf(60))
                    .build(),
            "205_122_2025-06-26", Training.builder()
                    .id(3L)
                    .trainer(Trainer.builder().id(205L).build())
                    .trainee(Trainee.builder().id(122L).build())
                    .trainingName("CrossFit Blast")
                    .trainingType(new TrainingType(3L, "CrossFit"))
                    .trainingDate(LocalDate.of(2025, 6, 26))
                    .trainingDuration(BigDecimal.valueOf(75))
                    .build(),
            "206_123_2025-06-25", Training.builder()
                    .id(4L)
                    .trainer(Trainer.builder().id(206L).build())
                    .trainee(Trainee.builder().id(123L).build())
                    .trainingName("Pilates Session")
                    .trainingType(new TrainingType(4L, "Pilates"))
                    .trainingDate(LocalDate.of(2025, 6, 25))
                    .trainingDuration(BigDecimal.valueOf(50))
                    .build(),
            "207_124_2025-06-24", Training.builder()
                    .id(5L)
                    .trainer(Trainer.builder().id(207L).build())
                    .trainee(Trainee.builder().id(124L).build())
                    .trainingName("Boxing Drills")
                    .trainingType(new TrainingType(5L, "Boxing"))
                    .trainingDate(LocalDate.of(2025, 6, 24))
                    .trainingDuration(BigDecimal.valueOf(90))
                    .build()
    );

}
