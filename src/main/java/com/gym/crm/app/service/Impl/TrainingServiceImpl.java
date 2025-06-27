package com.gym.crm.app.service.Impl;

import com.gym.crm.app.domain.dto.TrainingDto;
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
import java.util.stream.Collectors;

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
                .map(t -> modelMapper.map(t, TrainingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingDto> getTrainingByTrainerId(int trainerId) {
        List<Training> trainings = trainingRepository.findByTrainerId(trainerId);

        if (trainings.isEmpty()) {
            throw new EntityNotFoundException("Training not found!");
        }
        return trainings.stream()
                .map(t -> modelMapper.map(t, TrainingDto.class))
                .toList();
    }

    @Override
    public List<TrainingDto> getTrainingByTraineeId(int traineeId) {
        List<Training> trainings = trainingRepository.findByTraineeId(traineeId);

        if (trainings.isEmpty()) {
            throw new EntityNotFoundException("Training not found!");
        }
        return trainings.stream()
                .map(t -> modelMapper.map(t, TrainingDto.class))
                .toList();
    }

    @Override
    public List<TrainingDto> getTrainingByDate(LocalDate date) {
        List<Training> trainings = trainingRepository.findByDate(date);

        if (trainings.isEmpty()) {
            throw new EntityNotFoundException("Training not found!");
        }
        return trainings.stream()
                .map(t -> modelMapper.map(t, TrainingDto.class))
                .toList();
    }

    @Override
    public List<TrainingDto> getTrainingByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date) {
        List<Training> trainings = trainingRepository.findByTrainerId(trainerId).stream()
                .filter(t -> t.getTraineeId() == traineeId)
                .filter(t -> t.getTrainingDate().equals(date))
                .toList();

        if (trainings.isEmpty()) {
            throw new EntityNotFoundException("Training not found");
        }
        return trainings.stream()
                .map(t -> modelMapper.map(t, TrainingDto.class))
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
        return modelMapper.map(getTrainingByTrainerAndTraineeAndDate(training.getTrainerId(),
                training.getTraineeId(), training.getTrainingDate()), TrainingDto.class);
    }

    @Override
    public TrainingDto updateTraining(int trainerId, int traineeId, LocalDate date, TrainingDto trainingDto) throws UnacceptableOperationException {
        Training trainingToUpd;

        try {
            trainingToUpd = trainingRepository.findByTrainerAndTraineeAndDate(trainerId, traineeId, date).get(0);
        } catch (Exception e) {
            throw new EntityNotFoundException("Training not found!");
        }
        trainingToUpd.setTrainerId(trainingDto.getTrainerId());
        trainingToUpd.setTraineeId(trainingDto.getTraineeId());
        trainingToUpd.setTrainingName(trainingDto.getTrainingName());
        trainingToUpd.setTrainingType(trainingDto.getTrainingType());
        trainingToUpd.setTrainingDate(trainingDto.getTrainingDate());
        trainingToUpd.setTrainingDuration(trainingDto.getTrainingDuration());

        try {
            trainingRepository.save(trainingToUpd);
        } catch (Exception e) {
            throw new UnacceptableOperationException("Unable to update training!");
        }
        return modelMapper.map(trainingToUpd, TrainingDto.class);
    }

    @Override
    public void deleteTrainingByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date) throws UnacceptableOperationException {
        try {
            trainingRepository.deleteByTrainerAndTraineeAndDate(trainerId, traineeId, date);
        } catch (Exception e) {
            throw new UnacceptableOperationException("Unable to delete training!");
        }
    }
}
