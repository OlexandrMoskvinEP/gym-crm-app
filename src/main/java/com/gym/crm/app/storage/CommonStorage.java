package com.gym.crm.app.storage;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.storage.JsonStorageHandler.Namespace;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Component
public class CommonStorage {
    private Map<String, Map<String, ?>>storage;

    private final Map<String, Trainer> trainerStorage;
    private final Map<String, Trainee> traineeStorage;
    private final Map<String, Training> trainingStorage;

    private final JsonStorageHandler jsonStorageHandler;

    public CommonStorage(Map<String, Trainer> trainerStorage, Map<String, Trainee> traineeStorage, Map<String, Training> trainingStorage, JsonStorageHandler jsonStorageHandler) {
        this.trainerStorage = trainerStorage;
        this.traineeStorage = traineeStorage;
        this.trainingStorage = trainingStorage;
        this.jsonStorageHandler = jsonStorageHandler;
    }

    @PostConstruct
    public void init() {
        storage =  jsonStorageHandler.loadRawStorage();

        trainerStorage.putAll(jsonStorageHandler.parseSection(storage, Namespace.TRAINER.name(), Trainer.class));
        traineeStorage.putAll(jsonStorageHandler.parseSection(storage, Namespace.TRAINEE.name(), Trainee.class));
        trainingStorage.putAll(jsonStorageHandler.parseSection(storage, Namespace.TRAINING.name(), Training.class));
    }

    @PreDestroy
    public void shutdown() throws UnacceptableOperationException {
        jsonStorageHandler.save(trainerStorage, traineeStorage, trainingStorage);
    }
}
