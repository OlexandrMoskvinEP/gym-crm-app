package com.gym.crm.app.service.impl;

import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.exception.DataBaseErrorException;
import com.gym.crm.app.mapper.TrainingMapper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.repository.TrainingTypeRepository;
import com.gym.crm.app.repository.search.TraineeTrainingQueryBuilder;
import com.gym.crm.app.repository.search.TrainerTrainingQueryBuilder;
import com.gym.crm.app.repository.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.repository.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.app.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
public class TrainingServiceImpl implements TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingRepository repository;
    private final TrainingMapper trainingMapper;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TraineeTrainingQueryBuilder traineeQueryBuilder;
    private final TrainerTrainingQueryBuilder trainerQueryBuilder;

    @Setter
    private ModelMapper modelMapper;

    @Override
    public List<TrainingDto> getAllTrainings() {
        return repository.findAll()
                .stream()
                .map(training -> modelMapper.map(training, TrainingDto.class))
                .toList();
    }

    @Override
    public TrainingDto addTraining(TrainingSaveRequest training) {
        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(training.getTrainingTypeName())
                .orElseThrow(() -> new DataBaseErrorException(format("Training type: %s not found", training.getTrainingTypeName())));
        Trainer trainer = trainerRepository.findById(Math.toIntExact(training.getTrainerId())).get();
        Trainee trainee = traineeRepository.findById(training.getTraineeId()).get();

        Training trainingToSave = Training.builder()
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .trainingType(trainingType)
                .trainer(trainer)
                .trainee(trainee)
                .build();

        logger.info("Adding training for trainer {} and trainee {} on {}",
                training.getTrainerId(), training.getTraineeId(), training.getTrainingDate());

        Training persistedTraining = repository.save(trainingToSave);

        logger.info("Training successfully added for trainer {} and trainee {} on {}",
                training.getTrainerId(), training.getTraineeId(), training.getTrainingDate());

        return getTrainingDtoFromEntity(persistedTraining);
    }

    @Override
    public TrainingDto updateTraining(TrainingSaveRequest training) {
        Training existing = repository.findAll().stream()
                .findFirst().orElseThrow(() -> new DataBaseErrorException("Training not found"));
        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(training.getTrainingName())
                .orElseThrow(() -> new DataBaseErrorException(format("Training type: %s not found", training.getTrainingTypeName())));

        Training updated = Training.builder()
                .trainingDate(existing.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .trainingName(training.getTrainingName())
                .trainingType(trainingType)
                .trainee(existing.getTrainee())
                .trainer(existing.getTrainer())
                .build();

        repository.save(updated);

        logger.info("Training for trainer {} and trainee {} on {} updated",
                updated.getTrainer(), updated.getTrainee(), updated.getTrainingDate());

        return modelMapper.map(updated, TrainingDto.class);
    }

    @Override
    public List<TrainingDto> getTraineeTrainingsByFilter(TraineeTrainingSearchFilter filter) {
        return repository.findAll(traineeQueryBuilder.build(filter))
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    @Override
    public List<TrainingDto> getTrainerTrainingsByFilter(TrainerTrainingSearchFilter filter) {
        return repository.findAll(trainerQueryBuilder.build(filter))
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    @Override
    public List<TrainingType> getTrainingTypes() {
        return trainingTypeRepository.findAll();
    }

    private TrainingDto getTrainingDtoFromEntity(Training training) {
        return new TrainingDto(training.getTrainer().getId(),
                training.getTrainee().getId(),
                training.getTrainingName(),
                training.getTrainingType(),
                training.getTrainingDate(),
                training.getTrainingDuration());
    }
}
