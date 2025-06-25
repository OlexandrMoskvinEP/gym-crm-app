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
public class JSONStorageHandler {
    @Value("${storage.file}")
    private String STORAGE_FILE_PATH;
    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, Object> globalStorage = new HashMap<>();
    @Getter
    private final Map<String, Trainer> trainerStorage = new HashMap<>();
    @Getter
    private final Map<String, Trainee> traineeStorage = new HashMap<>();
    @Getter
    private final Map<String, Training> trainingStorage = new HashMap<>();

    private static final String NS_TRAINER = "trn";
    private static final String NS_TRAINEE = "tee";
    private static final String NS_TRAINING = "tng";

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
