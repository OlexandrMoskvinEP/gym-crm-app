package com.gym.crm.app.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.app.domain.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "datasets/training_types.xml", cleanBefore = true, cleanAfter = true)
class TrainingTypeRepositoryTest extends AbstractRepositoryTest<TrainingTypeRepository> {

    @ParameterizedTest
    @CsvSource({
            "Yoga, 1",
            "Cardio, 2",
            "CrossFit, 3",
            "Stretching, 4",
            "Pilates, 5",
            "Bodybuilding, 6"
    })
    void shouldFindByName(String typeName, Long expectedId) {
        Optional<TrainingType> actual = repository.findByTrainingTypeName(typeName);

        assertTrue(actual.isPresent());
        assertEquals(expectedId, actual.get().getId());
        assertEquals(typeName, actual.get().getTrainingTypeName());
    }

    @Test
    void shouldReturnAllTrainingTypes() {
        TrainingType expected1 = new TrainingType(1L, "Yoga");
        TrainingType expected2 = new TrainingType(2L, "Cardio");
        TrainingType expected3 = new TrainingType(3L, "CrossFit");
        TrainingType expected4 = new TrainingType(4L, "Stretching");
        TrainingType expected5 = new TrainingType(5L, "Pilates");
        TrainingType expected6 = new TrainingType(6L, "Bodybuilding");

        List<TrainingType> actual = repository.findAll();

        assertEquals(6, actual.size());
        assertTrue(actual.contains(expected1));
        assertTrue(actual.contains(expected2));
        assertTrue(actual.contains(expected3));
        assertTrue(actual.contains(expected4));
        assertTrue(actual.contains(expected5));
        assertTrue(actual.contains(expected6));
    }

    @Test
    void shouldReturnEmptyIfNotFound() {
        Optional<TrainingType> result = repository.findByTrainingTypeName("NonExistingType");

        assertTrue(result.isEmpty());
    }
}