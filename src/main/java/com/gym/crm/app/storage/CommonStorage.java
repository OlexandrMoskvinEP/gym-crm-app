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

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        Map<Namespace, Map<String, ?>> loaded = jsonStorageHandler.loadEntitiesFromFile();

        ((Map<String, Trainer>) storage.get(Namespace.TRAINER))
                .putAll((Map<String, Trainer>) loaded.get(Namespace.TRAINER));

        ((Map<String, Trainee>) storage.get(Namespace.TRAINEE))
                .putAll((Map<String, Trainee>) loaded.get(Namespace.TRAINEE));

        ((Map<String, Training>) storage.get(Namespace.TRAINING))
                .putAll((Map<String, Training>) loaded.get(Namespace.TRAINING));
    }

    @PreDestroy
    public void shutdown() throws UnacceptableOperationException {
        jsonStorageHandler.save(storage);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Trainer> getTrainerStorage() {
        return (Map<String, Trainer>) storage.get(Namespace.TRAINER);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Trainee> getTraineeStorage() {
        return (Map<String, Trainee>) storage.get(Namespace.TRAINEE);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Training> getTrainingStorage() {
        return (Map<String, Training>) storage.get(Namespace.TRAINING);
    }
}
