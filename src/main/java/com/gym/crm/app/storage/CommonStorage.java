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

import static com.gym.crm.app.storage.JsonStorageHandler.Namespace.TRAINEE;
import static com.gym.crm.app.storage.JsonStorageHandler.Namespace.TRAINER;
import static com.gym.crm.app.storage.JsonStorageHandler.Namespace.TRAINING;

@Getter
@Component
public class CommonStorage {
    private final Map<Namespace, Map<String, ?>> storage = new HashMap<>();

    private final JsonStorageHandler jsonStorageHandler;

    @Autowired
    public CommonStorage(Map<String, Trainer> trainerStorage,
                         Map<String, Trainee> traineeStorage,
                         Map<String, Training> trainingStorage,
                         JsonStorageHandler jsonStorageHandler) {
        this.storage.put(TRAINER, trainerStorage);
        this.storage.put(TRAINEE, traineeStorage);
        this.storage.put(TRAINING, trainingStorage);
        this.jsonStorageHandler = jsonStorageHandler;
    }

    @PostConstruct
    public void init() {
        Map<Namespace, Map<String, ?>> loadedDataFromFile = jsonStorageHandler.loadEntitiesFromFile();
        storage.putAll(loadedDataFromFile);

        mergeStorageData(TRAINER, loadedDataFromFile);
        mergeStorageData(TRAINEE, loadedDataFromFile);
        mergeStorageData(TRAINING, loadedDataFromFile);
    }

    @SuppressWarnings("unchecked")
    private <T> void mergeStorageData(Namespace namespace, Map<Namespace, Map<String, ?>> loadedDataFromFile) {
        Map<String, T> storageData = (Map<String, T>) storage.get(namespace);
        Map<String, T> loadedData = (Map<String, T>) loadedDataFromFile.get(namespace);

        if (storageData != null && loadedData != null) {
            storageData.putAll(loadedData);
        }
    }

    @PreDestroy
    public void shutdown() throws UnacceptableOperationException {
        jsonStorageHandler.save(storage);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Trainer> getTrainerStorage() {
        return (Map<String, Trainer>) storage.get(TRAINER);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Trainee> getTraineeStorage() {
        return (Map<String, Trainee>) storage.get(TRAINEE);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Training> getTrainingStorage() {
        return (Map<String, Training>) storage.get(TRAINING);
    }
}
