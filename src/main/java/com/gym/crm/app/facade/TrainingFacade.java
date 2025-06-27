package com.gym.crm.app.facade;

import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.domain.dto.TrainingIdentityDto;
import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TrainingFacade {
    private final TrainingService trainingService;

    @Autowired
    public TrainingFacade(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    public List<TrainingDto> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    public List<TrainingDto> getTrainingByTrainerId(int trainerId) {
        return trainingService.getTrainingByTrainerId(trainerId);
    }

    public List<TrainingDto> getTrainingByTraineeId(int traineeId) {
        return trainingService.getTrainingByTraineeId(traineeId);
    }

    public List<TrainingDto> getTrainingByDate(LocalDate date) {
        return trainingService.getTrainingByDate(date);
    }

    public List<TrainingDto> getTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto) {
        return trainingService.getTrainingByTrainerAndTraineeAndDate(identityDto);
    }

    public TrainingDto addTraining(TrainingDto training) throws UnacceptableOperationException {
        return trainingService.addTraining(training);
    }

    public TrainingDto updateTraining(TrainingDto trainingDto) throws UnacceptableOperationException {
        return trainingService.updateTraining(trainingDto);
    }

    public void deleteTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto) throws UnacceptableOperationException {
        trainingService.deleteTrainingByTrainerAndTraineeAndDate(identityDto);
    }
}
