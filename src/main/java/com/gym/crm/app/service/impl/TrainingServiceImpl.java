package com.gym.crm.app.service.impl;

import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.mapper.TrainingMapper;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.repository.TrainingTypeRepository;
import com.gym.crm.app.repository.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.repository.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.app.service.TrainingService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
public class TrainingServiceImpl implements TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingTypeRepository trainingTypeRepository;
    private TrainingRepository repository;
    private ModelMapper modelMapper;
    private TrainingMapper trainingMapper;

    @Autowired
    public void setRepository(TrainingRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Autowired
    public void setTrainingMapper(TrainingMapper trainingMapper) {
        this.trainingMapper = trainingMapper;
    }

    @Override
    public List<TrainingDto> getAllTrainings() {
        return repository.findAll()
                .stream()
                .map(training -> modelMapper.map(training, TrainingDto.class))
                .toList();
    }

    @Override
    public TrainingDto addTraining(TrainingSaveRequest training) {
        TrainingType trainingType = trainingTypeRepository.findByName(training.getTrainingName())
                .orElseThrow(() -> new EntityNotFoundException(format("Training type: %s not found", training.getTrainingTypeName())));
        Training trainingToSave = mapDtoToEntity(training, trainingType);

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
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Training not found"));
        TrainingType trainingType = trainingTypeRepository.findByName(training.getTrainingName())
                .orElseThrow(() -> new EntityNotFoundException(format("Training type: %s not found", training.getTrainingTypeName())));

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
        return repository.findByTraineeCriteria(filter)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    @Override
    public List<TrainingDto> getTrainerTrainingsByFilter(TrainerTrainingSearchFilter filter) {
        return repository.findByTrainerCriteria(filter)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    private TrainingDto getTrainingDtoFromEntity(Training training) {
        return new TrainingDto(training.getTrainer().getId(),
                training.getTrainee().getId(),
                training.getTrainingName(),
                training.getTrainingType(),
                training.getTrainingDate(),
                training.getTrainingDuration());
    }

    private static Training mapDtoToEntity(TrainingSaveRequest source, TrainingType trainingType) {
        return Training.builder()
                .trainingName(source.getTrainingName())
                .trainingDate(source.getTrainingDate())
                .trainingDuration(source.getTrainingDuration())

                .trainer(Trainer.builder().id(source.getTrainerId()).build())
                .trainee(Trainee.builder().id(source.getTraineeId()).build())
                .trainingType(trainingType)
                .trainingName(source.getTrainingName())
                .build();
    }
}
