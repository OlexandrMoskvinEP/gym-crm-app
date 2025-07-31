package com.gym.crm.app.repository.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "datasets/trainers.xml", cleanBefore = true, cleanAfter = true)
public class TrainerRepositoryImplTest extends AbstractRepositoryTest<TrainerRepository> {

    @Test
    void shouldReturnAllTrainers() {
        List<Trainer> all = repository.findAll();

        assertEquals(3, all.size());

        Trainer t1 = all.get(0);
        assertEquals(1L, t1.getId());
        assertEquals("boris.krasnov", t1.getUser().getUsername());
        assertEquals("Boris", t1.getUser().getFirstName());
        assertEquals("Krasnov", t1.getUser().getLastName());

        Trainer t2 = all.get(1);
        assertEquals(2L, t2.getId());
        assertEquals("mykyta.solntcev", t2.getUser().getUsername());
        assertEquals("Mykyta", t2.getUser().getFirstName());
        assertEquals("Solntcev", t2.getUser().getLastName());

        Trainer t3 = all.get(2);
        assertEquals(3L, t3.getId());
        assertEquals("arnold.schwarzenegger", t3.getUser().getUsername());
        assertEquals("Arnold", t3.getUser().getFirstName());
        assertEquals("Schwarzenegger", t3.getUser().getLastName());
    }

    @ParameterizedTest
    @MethodSource("provideTrainersForIdTest")
    void shouldFindTrainerById(Long id, String username, String firstName, String lastName) {
        Optional<Trainer> actual = repository.findById(Math.toIntExact(id));

        assertTrue(actual.isPresent());
        Trainer trainer = actual.get();

        assertEquals(id, trainer.getId());
        assertNotNull(trainer.getUser());
        assertEquals(username, trainer.getUser().getUsername());
        assertEquals(firstName, trainer.getUser().getFirstName());
        assertEquals(lastName, trainer.getUser().getLastName());
    }

    @ParameterizedTest
    @MethodSource("provideUsernames")
    void shouldFindByUsername(String username, Long expectedId) {
        Optional<Trainer> actual = repository.findByUserUsername(username);

        assertTrue(actual.isPresent());
        assertEquals(expectedId, actual.get().getId());
        assertEquals(username, actual.get().getUser().getUsername());
    }

    @Test
    void shouldSaveTrainer() {
        Trainer toSave = constructTrainer();

        Long actual = repository.save(toSave).getId();

        Trainer found = repository.findByUserUsername("trainer.test").orElse(null);

        assertNotNull(found);
        assertNotNull(found.getUser());
        assertEquals(actual, found.getId());
        assertEquals("Trainer1", found.getUser().getFirstName());
        assertEquals("Test", found.getUser().getLastName());
        assertEquals("trainer.test", found.getUser().getUsername());
        assertEquals("Pwd123!@#", found.getUser().getPassword());
        assertTrue(found.getUser().getIsActive());

        assertNotNull(found.getSpecialization());
        assertEquals("Yoga", found.getSpecialization().getTrainingTypeName());
    }

    @Test
    void shouldUpdateTrainer() {
        Trainer original = repository.findByUserUsername("boris.krasnov")
                .orElseThrow(() -> new IllegalArgumentException("Test user not found"));

        Trainer toUpdate = original.toBuilder()
                .user(original.getUser().toBuilder().firstName("Updated").build())
                .build();

        repository.save(toUpdate);

        Trainer updated = repository.findByUserUsername("boris.krasnov").orElse(null);

        assertNotNull(updated);
        assertEquals("Updated", updated.getUser().getFirstName());
    }

    @Test
    void shouldDeleteEntityByUsername() {
        repository.deleteByUserUsername("arnold.schwarzenegger");

        Optional<Trainer> found = repository.findByUserUsername("arnold.schwarzenegger");
        assertFalse(found.isPresent());
    }

    private static Stream<Arguments> provideTrainersForIdTest() {
        return Stream.of(
                Arguments.of(1L, "boris.krasnov", "Boris", "Krasnov"),
                Arguments.of(2L, "mykyta.solntcev", "Mykyta", "Solntcev"),
                Arguments.of(3L, "arnold.schwarzenegger", "Arnold", "Schwarzenegger")
        );
    }

    private static Stream<Arguments> provideUsernames() {
        return Stream.of(
                Arguments.of("boris.krasnov", 1L),
                Arguments.of("mykyta.solntcev", 2L),
                Arguments.of("arnold.schwarzenegger", 3L)
        );
    }

    private static Trainer constructTrainer() {
        User user = User.builder()
                .firstName("Trainer1")
                .lastName("Test")
                .username("trainer.test")
                .password("Pwd123!@#")
                .isActive(true)
                .build();

        return Trainer.builder()
                .user(user)
                .specialization(TrainingType.builder().id(1L).trainingTypeName("Yoga").build())
                .build();
    }
}
