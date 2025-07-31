package com.gym.crm.app.repository.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.TraineeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "datasets/trainees.xml", cleanBefore = true, cleanAfter = true)
public class TraineeRepositoryImplTest extends AbstractRepositoryTest<TraineeRepository> {

    @Test
    void shouldReturnAllTrainees() {
        List<Trainee> all = repository.findAll();

        assertEquals(3, all.size());

        Trainee trainee1 = all.get(0);
        assertEquals(1L, trainee1.getId());
        assertEquals("Kyiv, Ukraine", trainee1.getAddress());
        assertEquals(LocalDate.of(1995, 3, 15), trainee1.getDateOfBirth());
        assertNotNull(trainee1.getUser());
        assertEquals("john.smith", trainee1.getUser().getUsername());
        assertEquals("John", trainee1.getUser().getFirstName());
        assertEquals("Smith", trainee1.getUser().getLastName());

        Trainee trainee2 = all.get(1);
        assertEquals(2L, trainee2.getId());
        assertEquals("Lviv, Ukraine", trainee2.getAddress());
        assertEquals(LocalDate.of(1998, 7, 10), trainee2.getDateOfBirth());
        assertNotNull(trainee2.getUser());
        assertEquals("olga.ivanova", trainee2.getUser().getUsername());
        assertEquals("Olga", trainee2.getUser().getFirstName());
        assertEquals("Ivanova", trainee2.getUser().getLastName());

        Trainee trainee3 = all.get(2);
        assertEquals(3L, trainee3.getId());
        assertEquals("Dnipro, Ukraine", trainee3.getAddress());
        assertEquals(LocalDate.of(2000, 11, 1), trainee3.getDateOfBirth());
        assertNotNull(trainee3.getUser());
        assertEquals("irina.petrova", trainee3.getUser().getUsername());
        assertEquals("Irina", trainee3.getUser().getFirstName());
        assertEquals("Petrova", trainee3.getUser().getLastName());
    }

    @ParameterizedTest
    @MethodSource("provideTraineesForIdTest")
    void shouldFindTraineeById(Long id, String username, String firstName, String lastName,
                               String address, LocalDate dob) {
        Optional<Trainee> actual = repository.findById(id);

        assertTrue(actual.isPresent());
        Trainee trainee = actual.get();

        assertEquals(id, trainee.getId());
        assertEquals(address, trainee.getAddress());
        assertEquals(dob, trainee.getDateOfBirth());
        assertNotNull(trainee.getUser());
        assertEquals(username, trainee.getUser().getUsername());
        assertEquals(firstName, trainee.getUser().getFirstName());
        assertEquals(lastName, trainee.getUser().getLastName());
    }

    @ParameterizedTest
    @MethodSource("provideUsernames")
    void shouldFindByUsername(String username, Long expectedId) {
        Optional<Trainee> actual = repository.findByUserUsername(username);

        assertTrue(actual.isPresent());
        assertEquals(expectedId, actual.get().getId());
        assertEquals(username, actual.get().getUser().getUsername());
    }

    @Test
    void shouldSaveTrainee() {
        Trainee toSave = constructTrainee();
        Long actual = repository.save(toSave).getId();

        Trainee found = repository.findById(actual).orElse(null);

        assertNotNull(found);
        assertEquals(toSave.getAddress(), found.getAddress());
        assertEquals(toSave.getDateOfBirth(), found.getDateOfBirth());

        assertNotNull(found.getUser());
        assertEquals(toSave.getUser().getUsername(), found.getUser().getUsername());
        assertEquals(toSave.getUser().getFirstName(), found.getUser().getFirstName());
        assertEquals(toSave.getUser().getLastName(), found.getUser().getLastName());
    }

    @Test
    void shouldUpdateTrainee() {
        Trainee original = repository.findByUserUsername("olga.ivanova")
                .orElseThrow(() -> new IllegalArgumentException("Test user was not found"));
        Trainee toUpdate = original.toBuilder()
                .address("Updated Address")
                .dateOfBirth(LocalDate.of(2001, 2, 2))
                .build();

        repository.save(toUpdate);

        Trainee updated = repository.findByUserUsername("olga.ivanova").orElse(null);

        assertNotNull(updated);
        assertEquals(2L, updated.getId());
        assertEquals("Updated Address", updated.getAddress());
        assertEquals(LocalDate.of(2001, 2, 2), updated.getDateOfBirth());
        assertNotNull(updated.getUser());
        assertEquals("olga.ivanova", updated.getUser().getUsername());
        assertEquals("Olga", updated.getUser().getFirstName());
        assertEquals("Ivanova", updated.getUser().getLastName());
    }

    @Test
    void shouldDeleteEntityById() {
        repository.deleteById(1L);

        Optional<Trainee> foundTrainee = repository.findByUserUsername("john.smith");
        assertFalse(foundTrainee.isPresent());
    }

    @Test
    void shouldDeleteEntityByUsername() {
        String existUsername = "john.smith";

        repository.deleteByUserUsername(existUsername);

        Optional<Trainee> foundTrainee = repository.findByUserUsername("john.smith");
        assertFalse(foundTrainee.isPresent());
    }

    private static Stream<Arguments> provideTraineesForIdTest() {
        return Stream.of(
                Arguments.of(1L, "john.smith", "John", "Smith", "Kyiv, Ukraine", LocalDate.of(1995, 3, 15)),
                Arguments.of(2L, "olga.ivanova", "Olga", "Ivanova", "Lviv, Ukraine", LocalDate.of(1998, 7, 10)),
                Arguments.of(3L, "irina.petrova", "Irina", "Petrova", "Dnipro, Ukraine", LocalDate.of(2000, 11, 1))
        );
    }

    private static Stream<Arguments> provideUsernames() {
        return Stream.of(
                Arguments.of("john.smith", 1L),
                Arguments.of("olga.ivanova", 2L),
                Arguments.of("irina.petrova", 3L)
        );
    }

    private static Trainee constructTrainee() {
        int id = new Random().nextInt(200);
        User user = User.builder()
                .firstName("Trainee" + id)
                .lastName("Test")
                .username("trainee" + id)
                .password("Pwd123!@#")
                .isActive(true)
                .build();

        return Trainee.builder()
                .dateOfBirth(LocalDate.of(1990 + id, 1, 13))
                .address("Test address " + id)
                .user(user)
                .build();
    }

    private Set<Trainer> buildExpectedTrainers() {
        Set<Trainer> trainers = new HashSet<>();
        trainers.add(buildTrainer(1L, "John", "Doe", "john.doe", 1L, "Cardio"));
        trainers.add(buildTrainer(2L, "Mike", "Tyson", "mike.tyson", 2L, "Crossfit"));

        return trainers;
    }

    private Trainer buildArnoldTrainer() {
        return buildTrainer(6L, "Arnold", "Schwarzenegger", "arnold.schwarzenegger", 6L, "Bodybuilding");
    }

    private Trainer buildTrainer(Long trainerId,
                                 String firstName,
                                 String lastName,
                                 String username,
                                 Long specializationId,
                                 String specializationName) {
        return Trainer.builder()
                .id(trainerId)
                .user(User.builder()
                        .id(trainerId)
                        .firstName(firstName)
                        .lastName(lastName)
                        .username(username)
                        .password("123")
                        .isActive(true)
                        .build())
                .specialization(TrainingType.builder()
                        .id(specializationId)
                        .trainingTypeName(specializationName)
                        .build())
                .build();
    }
}
