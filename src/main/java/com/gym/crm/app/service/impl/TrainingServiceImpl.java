package com.gym.crm.app.service.impl;

import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.domain.dto.TrainingIdentityDto;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.mapper.TrainingMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    public List<TrainingDto> getAllTrainings() {
        return trainingRepository.findAll()
                .stream()
                .map(training -> modelMapper.map(training, TrainingDto.class))
                .toList();
    }

    @Override
    public List<TrainingDto> getTrainingByTrainerId(int trainerId) {
        List<Training> trainings = trainingRepository.findByTrainerId(trainerId);

        if (trainings.isEmpty()) {
            throw new EntityNotFoundException("Training not found!");
        }

        return trainings.stream()
                .map(training -> modelMapper.map(training, TrainingDto.class))
                .toList();
    }

    @Override
    public List<TrainingDto> getTrainingByTraineeId(int traineeId) {
        List<Training> trainings = trainingRepository.findByTraineeId(traineeId);

        if (trainings.isEmpty()) {
            throw new EntityNotFoundException("Training not found!");
        }

        return trainings.stream()
                .map(training -> modelMapper.map(training, TrainingDto.class))
                .toList();
    }

    @Override
    public List<TrainingDto> getTrainingByDate(LocalDate date) {
        List<Training> trainings = trainingRepository.findByDate(date);

        if (trainings.isEmpty()) {
            throw new EntityNotFoundException("Training not found!");
        }

        return trainings.stream()
                .map(training -> modelMapper.map(training, TrainingDto.class))
                .toList();
    }

    @Override
    public Optional<TrainingDto> getTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto) {
        Optional<Training> training = Optional.ofNullable(trainingRepository.findByTrainerAndTraineeAndDate(
                        identityDto.getTrainerId(),
                        identityDto.getTraineeId(),
                        identityDto.getTrainingDate())
                .orElseThrow(() -> new EntityNotFoundException("Training not found!")));

        return training.map(training1 -> modelMapper.map(training1, TrainingDto.class));
    }

    @Override
    public TrainingDto addTraining(TrainingDto training) {
        Training trainingToSave = TrainingMapper.mapDtoToEntity(training);

        logger.info("Adding training for trainer {} and trainee {} on {}",
                training.getTrainerId(), training.getTraineeId(), training.getTrainingDate());

        Training persistedTraining = trainingRepository.saveTraining(trainingToSave);

        logger.info("Training successfully added for trainer {} and trainee {} on {}",
                training.getTrainerId(), training.getTraineeId(), training.getTrainingDate());

        return modelMapper.map(persistedTraining, TrainingDto.class);
    }

    @Override
    public TrainingDto updateTraining(TrainingDto trainingDto) {
        Training existing = trainingRepository.findByTrainerAndTraineeAndDate(
                        trainingDto.getTrainerId(),
                        trainingDto.getTraineeId(),
                        trainingDto.getTrainingDate())
                .orElseThrow(() -> new EntityNotFoundException("Training not found!"));

        Training updated = Training.builder()
                .trainingDate(existing.getTrainingDate())
                .trainingDuration(BigDecimal.valueOf(trainingDto.getTrainingDuration()))
                .trainingName(trainingDto.getTrainingName())
                .trainingType(trainingDto.getTrainingType())
                .trainee(existing.getTrainee())
                .trainer(existing.getTrainer())
                .build();

        trainingRepository.saveTraining(updated);

        logger.info("Training for trainer {} and trainee {} on {} updated",
                updated.getTrainer(), updated.getTrainee(), updated.getTrainingDate());

        return modelMapper.map(updated, TrainingDto.class);
    }

    @Override
    public void deleteTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto) {
        if (trainingRepository.findByTrainerAndTraineeAndDate(
                identityDto.getTrainerId(), identityDto.getTraineeId(), identityDto.getTrainingDate()).isEmpty()) {
            throw new EntityNotFoundException("Training not found!");
        }

        trainingRepository.deleteByTrainerAndTraineeAndDate(identityDto.getTrainerId(),
                identityDto.getTraineeId(), identityDto.getTrainingDate());

        logger.info("Training for trainer {} and trainee {} on {} deleted",
                identityDto.getTrainerId(), identityDto.getTraineeId(), identityDto.getTrainingDate());
    }
}
