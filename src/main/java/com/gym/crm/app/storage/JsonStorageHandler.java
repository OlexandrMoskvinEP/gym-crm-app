package com.gym.crm.app.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.exception.UnacceptableOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class JsonStorageHandler {
    @Value("${storage.file}")
    private String storageFile;

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public HashMap<String, Object> loadRawStorage() {
        try (InputStream input = new FileInputStream(storageFile)) {
            return objectMapper.readValue(input, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from JSON file", e);
        }
    }

    public <T> Map<String, T> parseSection(Map<String, Object> raw, String key, Class<T> clazz) {
        Map<String, T> result = new HashMap<>();
        Object rawSection = raw.get(key);

        if (rawSection instanceof Map<?, ?> sectionMap) {
            for (var entry : sectionMap.entrySet()) {
                String id = entry.getKey().toString();
                T obj = objectMapper.convertValue(entry.getValue(), clazz);
                result.put(id, obj);
            }
        }
        return result;
    }

    public void save(Map<String, Trainer> trainers,
                     Map<String, Trainee> trainees,
                     Map<String, Training> trainings) throws UnacceptableOperationException {
        Map<String, Object> toWrite = new HashMap<>();

        toWrite.put(Namespace.TRAINER.name(), trainers);
        toWrite.put(Namespace.TRAINEE.name(), trainees);
        toWrite.put(Namespace.TRAINING.name(), trainings);

        try (Writer writer = new FileWriter(storageFile)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, toWrite);
        } catch (IOException e) {
            throw new UnacceptableOperationException("Error occurred while try to write into the file!");
        }
    }

    public enum Namespace {
        TRAINER,
        TRAINEE,
        TRAINING
    }
}
