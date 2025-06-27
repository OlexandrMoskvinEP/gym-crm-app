package com.gym.crm.app.service.Impl;

import com.gym.crm.app.domain.dto.TrainerDto;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.service.TrainerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {
    private TrainerRepository trainerRepository;
    private ModelMapper modelMapper;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TrainerDto> getAllTrainers() {
        return List.of();
    }

    @Override
    public TrainerDto getTrainerByUsername(String username) {
        return null;
    }

    @Override
    public TrainerDto addTrainer(TrainerDto trainerDto) {
        return null;
    }

    @Override
    public TrainerDto updateTrainerByUsername(String username) {
        return null;
    }

    @Override
    public void deleteTrainerByUsername(String username) {

    }
}
