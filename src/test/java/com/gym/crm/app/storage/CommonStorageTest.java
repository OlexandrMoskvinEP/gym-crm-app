package com.gym.crm.app.storage;

import com.gym.crm.app.data.TestData;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.gym.crm.app.storage.JsonStorageHandler.Namespace.TRAINEE;
import static com.gym.crm.app.storage.JsonStorageHandler.Namespace.TRAINER;
import static com.gym.crm.app.storage.JsonStorageHandler.Namespace.TRAINING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommonStorageTest {
    private final TestData data = new TestData();

    private Map<String, Trainer> trainerMap;
    private Map<String, Trainee> traineeMap;
    private Map<String, Training> trainingMap;

    private JsonStorageHandler jsonStorageHandler;
    private CommonStorage commonStorage;

    @BeforeEach
    void setUp() {
        trainerMap = new HashMap<>(data.getTRAINER_STORAGE());
        traineeMap = new HashMap<>(data.getTRAINEE_STORAGE());
        trainingMap = new HashMap<>(data.getTRAINING_STORAGE());

        jsonStorageHandler = new JsonStorageHandler();
        commonStorage = new CommonStorage(trainerMap, traineeMap, trainingMap, jsonStorageHandler);
    }

    @Test
    void getTrainerStorage() {
        Map<String, Trainer> actual = commonStorage.getTrainerStorage();

        assertNotNull(actual);
        assertEquals(trainerMap, actual);
    }

    @Test
    void getTraineeStorage() {
        Map<String, Trainee> actual = commonStorage.getTraineeStorage();

        assertNotNull(actual);
        assertEquals(traineeMap, actual);
    }

    @Test
    void getTrainingStorage() {
        Map<String, Training> actual = commonStorage.getTrainingStorage();

        assertNotNull(actual);
        assertEquals(trainingMap, actual);
    }

    @Test
    void getStorage() {
        Map<JsonStorageHandler.Namespace, Map<String, ?>> storage = commonStorage.getStorage();

        assertNotNull(storage);
        assertEquals(trainerMap, storage.get(TRAINER));
        assertEquals(traineeMap, storage.get(TRAINEE));
        assertEquals(trainingMap, storage.get(TRAINING));
    }

    @Test
    void getJsonStorageHandler() {
        JsonStorageHandler actual = commonStorage.getJsonStorageHandler();

        assertNotNull(actual);
        assertEquals(jsonStorageHandler, actual);
    }
}