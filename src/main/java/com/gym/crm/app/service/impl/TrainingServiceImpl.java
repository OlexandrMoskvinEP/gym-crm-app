package com.gym.crm.app.service.impl;

import com.gym.crm.app.domain.dto.training.TrainingResponse;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.service.TrainingService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingRepository trainingRepository;
    private ModelMapper modelMapper;

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TrainingResponse> getAllTrainings() {
        return trainingRepository.findAll()
                .stream()
                .map(training -> modelMapper.map(training, TrainingResponse.class))
                .toList();
    }

    @Override
    public TrainingResponse addTraining(TrainingResponse training) {
        Training trainingToSave = mapDtoToEntity(training);
        logger.info("Adding training for trainer {} and trainee {} on {}",
                training.getTrainerId(), training.getTraineeId(), training.getTrainingDate());

        Training persistedTraining = trainingRepository.save(trainingToSave);

        logger.info("Training successfully added for trainer {} and trainee {} on {}",
                training.getTrainerId(), training.getTraineeId(), training.getTrainingDate());

        return getTrainingDtoFromEntity(persistedTraining);
    }

    @Override
    public TrainingResponse updateTraining(TrainingResponse trainingResponse) {
        Training existing = trainingRepository.findAll().stream()
                .findFirst().orElseThrow(
                        () -> new EntityNotFoundException("Training not found"));

        Training updated = Training.builder()
                .trainingDate(existing.getTrainingDate())
                .trainingDuration(trainingResponse.getTrainingDuration())
                .trainingName(trainingResponse.getTrainingName())
                .trainingType(trainingResponse.getTrainingType())
                .trainee(existing.getTrainee())
                .trainer(existing.getTrainer())
                .build();

        trainingRepository.save(updated);

        logger.info("Training for trainer {} and trainee {} on {} updated",
                updated.getTrainer(), updated.getTrainee(), updated.getTrainingDate());

        return modelMapper.map(updated, TrainingResponse.class);
    }

    private TrainingResponse getTrainingDtoFromEntity(Training training) {
        return new TrainingResponse(training.getTrainer().getId(),
                training.getTrainee().getId(),
                training.getTrainingName(),
                training.getTrainingType(),
                training.getTrainingDate(),
                training.getTrainingDuration());
    }

    private static Training mapDtoToEntity(TrainingResponse source) {
        return Training.builder()
                .trainingName(source.getTrainingName())
                .trainingDate(source.getTrainingDate())
                .trainingDuration(source.getTrainingDuration())

                .trainer(Trainer.builder().id(source.getTrainerId()).build())
                .trainee(Trainee.builder().id(source.getTraineeId()).build())
                .trainingType(source.getTrainingType())
                .trainingName(source.getTrainingName())
                .build();
    }
}
