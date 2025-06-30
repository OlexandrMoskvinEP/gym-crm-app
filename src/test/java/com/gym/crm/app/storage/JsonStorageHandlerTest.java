package com.gym.crm.app.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Map;

import static com.gym.crm.app.storage.JsonStorageHandler.Namespace.TRAINEE;
import static com.gym.crm.app.storage.JsonStorageHandler.Namespace.TRAINER;
import static com.gym.crm.app.storage.JsonStorageHandler.Namespace.TRAINING;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JsonStorageHandlerTest {
    private static JsonStorageHandler storageHandler;
    private static Map<JsonStorageHandler.Namespace, Map<String, ?>> fileStorage;

    @BeforeAll
    static void setUp() throws IOException {
        String path = new ClassPathResource("storage.json").getFile().getAbsolutePath();

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        storageHandler = new JsonStorageHandler();
        storageHandler.setObjectMapper(objectMapper);
        storageHandler.setStorageFilePath(path);

        fileStorage = storageHandler.loadEntitiesFromFile();
    }

    @Test
    void shouldLoadEntitiesFromFile() {
        Map<String, ?> traineeMap = fileStorage.get(TRAINEE);
        Map<String, ?> trainerMap = fileStorage.get(TRAINER);
        Map<String, ?> trainingMap = fileStorage.get(TRAINING);

        assertNotNull(traineeMap);
        assertFalse(traineeMap.isEmpty());

        assertNotNull(trainerMap);
        assertFalse(trainerMap.isEmpty());

        assertNotNull(trainingMap);
        assertFalse(trainingMap.isEmpty());
    }

    @Test
    void parseSection() {
        assertTrue(fileStorage.containsKey(TRAINER));
        assertTrue(fileStorage.containsKey(TRAINEE));
        assertTrue(fileStorage.containsKey(TRAINING));
    }
}