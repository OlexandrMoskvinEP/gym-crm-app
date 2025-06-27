package com.gym.crm.app.service.Impl;

import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.domain.dto.TrainingIdentityDto;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.service.TrainingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {
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
    public List<TrainingDto> getTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto) {
        List<Training> trainings = trainingRepository.findByTrainerId(identityDto.getTrainerId()).stream()
                .filter(training -> training.getTraineeId() == identityDto.getTraineeId())
                .filter(training -> training.getTrainingDate().equals(identityDto.getTrainingDate()))
                .toList();

        if (trainings.isEmpty()) {
            throw new EntityNotFoundException("Training not found");
        }
        return trainings.stream()
                .map(training -> modelMapper.map(training, TrainingDto.class))
                .toList();
    }

    @Override
    public TrainingDto addTraining(TrainingDto training) throws UnacceptableOperationException {
        Training trainingToSave = modelMapper.map(training, Training.class);

        try {
            trainingRepository.save(trainingToSave);
        } catch (RuntimeException e) {
            throw new UnacceptableOperationException("Failed while saving training!");
        }
        TrainingIdentityDto identityDto = modelMapper.map(trainingToSave, TrainingIdentityDto.class);

        return getTrainingByTrainerAndTraineeAndDate(identityDto).get(0);
    }

    @Override
    public TrainingDto updateTraining(TrainingDto trainingDto) throws UnacceptableOperationException {
        Training trainingToUpdate;

        try {
            trainingToUpdate = trainingRepository.findByTrainerAndTraineeAndDate(
                    trainingDto.getTrainerId(),
                    trainingDto.getTraineeId(),
                    trainingDto.getTrainingDate()).get(0);
        } catch (Exception e) {
            throw new EntityNotFoundException("Training not found!");
        }

        trainingToUpdate.setTrainingName(trainingDto.getTrainingName());
        trainingToUpdate.setTrainingType(trainingDto.getTrainingType());
        trainingToUpdate.setTrainingDuration(trainingDto.getTrainingDuration());

        try {
            trainingRepository.save(trainingToUpdate);
        } catch (Exception e) {
            throw new UnacceptableOperationException("Unable to update training!");
        }
        return modelMapper.map(trainingToUpdate, TrainingDto.class);
    }

    @Override
    public void deleteTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto) throws UnacceptableOperationException {
        try {
            trainingRepository.deleteByTrainerAndTraineeAndDate(identityDto.getTrainerId(),
                    identityDto.getTraineeId(), identityDto.getTrainingDate());
        } catch (Exception e) {
            throw new UnacceptableOperationException("Unable to delete training!");
        }
    }
}
