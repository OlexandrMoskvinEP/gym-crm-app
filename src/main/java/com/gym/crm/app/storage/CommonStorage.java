package com.gym.crm.app.storage;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.storage.JsonStorageHandler.Namespace;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class CommonStorage {
    private HashMap<String, Object> storage;

    private Map<String, Trainer> trainerStorage;
    private Map<String, Trainee> traineeStorage;
    private Map<String, Training> trainingStorage;

    private JsonStorageHandler jsonStorageHandler;

    @Autowired
    public void setJsonStorageHandler(JsonStorageHandler jsonStorageHandler) {
        this.jsonStorageHandler = jsonStorageHandler;
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
        storage = jsonStorageHandler.loadRawStorage();

        trainerStorage.putAll(jsonStorageHandler.parseSection(storage, Namespace.TRAINER.name(), Trainer.class));
        traineeStorage.putAll(jsonStorageHandler.parseSection(storage, Namespace.TRAINEE.name(), Trainee.class));
        trainingStorage.putAll(jsonStorageHandler.parseSection(storage, Namespace.TRAINING.name(), Training.class));
    }

    @PreDestroy
    public void shutdown() throws UnacceptableOperationException {
        jsonStorageHandler.save(trainerStorage, traineeStorage, trainingStorage);
    }
}
