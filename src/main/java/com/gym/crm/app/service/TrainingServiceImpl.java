package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.repository.TrainingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService{
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
        return List.of();
    }

    @Override
    public List<TrainingDto> getTrainingByTrainerId(int trainerId) {
        return List.of();
    }

    @Override
    public List<TrainingDto> getTrainingByByTraineeId(int trainerId) {
        return List.of();
    }

    @Override
    public List<TrainingDto> getTrainingByDate(LocalDate date) {
        return List.of();
    }

    @Override
    public List<TrainingDto> getTrainingByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date) {
        return List.of();
    }

    @Override
    public TrainingDto addTraining(TrainingDto training) {
        return null;
    }

    @Override
    public void deleteTrainingByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date) {

    }
}
