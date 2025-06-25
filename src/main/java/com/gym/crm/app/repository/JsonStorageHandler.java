package com.gym.crm.app.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class JsonStorageHandler {
    @Value("${storage.file}")
    private String STORAGE_FILE_PATH;
    private ObjectMapper objectMapper;
    private final Map<String, Object> globalStorage = new HashMap<>();
    private  Map<String, Trainer> trainerStorage;
    private  Map<String, Trainee> traineeStorage;
    private  Map<String, Training> trainingStorage;

    private static final String NS_TRAINER = "trn";
    private static final String NS_TRAINEE = "tee";
    private static final String NS_TRAINING = "tng";

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @Autowired
    public void setTrainerStorage(Map<String, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }
    @Autowired
    public void setTraineeStorage(Map<String, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }
    @Autowired
    public void setTrainingStorage(Map<String, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @PostConstruct
    public void init() {
        try (InputStream input = new FileInputStream(STORAGE_FILE_PATH)) {
            Map<String, Object> loaded = objectMapper.readValue(input, new TypeReference<>() {
            });
            if (loaded != null) {
                globalStorage.putAll(loaded);

                deserializeNamespace(NS_TRAINER, trainerStorage, Trainer.class);
                deserializeNamespace(NS_TRAINEE, traineeStorage, Trainee.class);
                deserializeNamespace(NS_TRAINING, trainingStorage, Training.class);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load storage from JSON", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        globalStorage.put(NS_TRAINER, trainerStorage);
        globalStorage.put(NS_TRAINEE, traineeStorage);
        globalStorage.put(NS_TRAINING, trainingStorage);

        try (Writer writer = new FileWriter(STORAGE_FILE_PATH)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, globalStorage);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write storage to JSON", e);
        }
    }

    private <T> void deserializeNamespace(String ns, Map<String, T> targetMap, Class<T> clazz) {
        Object raw = globalStorage.get(ns);
        if (raw instanceof Map<?, ?> rawMap) {
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                String key = entry.getKey().toString();
                T obj = objectMapper.convertValue(entry.getValue(), clazz);
                targetMap.put(key, obj);
            }
        }
    }
}
