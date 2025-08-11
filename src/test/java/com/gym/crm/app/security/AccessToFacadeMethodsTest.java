package com.gym.crm.app.security;

import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.mapper.TraineeMapper;
import com.gym.crm.app.mapper.TrainerMapper;
import com.gym.crm.app.mapper.TrainingMapper;
import com.gym.crm.app.mapper.TrainingTypeMapper;
import com.gym.crm.app.mapper.UserMapper;
import com.gym.crm.app.rest.TraineeUpdateResponse;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.common.UserProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AccessToFacadeMethodsTest.Cfg.class)
class AccessToFacadeMethodsTest {
    @TestConfiguration
    @EnableMethodSecurity(prePostEnabled = true)
    static class Cfg {
        @Bean
        GymFacade facade(TraineeService traineeService,
                         TrainerService trainerService,
                         TrainingService trainingService,
                         UserProfileService userProfileService,
                         TraineeMapper traineeMapper,
                         TrainerMapper trainerMapper,
                         TrainingMapper trainingMapper,
                         TrainingTypeMapper trainingTypeMapper,
                         UserMapper userMapper) {
            return new GymFacade(traineeService, trainerService, trainingService,
                    userProfileService, traineeMapper, trainerMapper,
                    trainingMapper, trainingTypeMapper, userMapper);
        }
    }

    @Autowired
    GymFacade facade;

    @MockBean
    TraineeService traineeService;
    @MockBean
    TrainerService trainerService;
    @MockBean
    TrainingService trainingService;
    @MockBean
    UserProfileService userProfileService;
    @MockBean
    TraineeMapper traineeMapper;
    @MockBean
    TrainerMapper trainerMapper;
    @MockBean
    TrainingMapper trainingMapper;
    @MockBean
    TrainingTypeMapper trainingTypeMapper;
    @MockBean
    UserMapper userMapper;

    @Test
    @WithMockUser(username = "vasyl", roles = "TRAINEE")
    void shouldAllowTraineeToUpdateHisProfile() {
        var updateRequest = new TraineeUpdateRequest();
        var updatedDTO = new TraineeDto();

        when(traineeService.updateTraineeByUsername("vasyl", updateRequest)).thenReturn(updatedDTO);
        when(traineeMapper.dtoToUpdateResponse(updatedDTO)).thenReturn(new TraineeUpdateResponse());

        facade.updateTraineeByUsername("vasyl", updateRequest);

        assertThatNoException()
                .isThrownBy(() -> facade.updateTraineeByUsername("vasyl", updateRequest));
    }

    @Test
    @WithMockUser(username = "vasyl", roles = "TRAINEE")
    void shouldForbidTraineeToUpdateTrainerProfile() {
        var updateRequest = new TrainerUpdateRequest();

        assertThatThrownBy(() -> facade.updateTrainerByUsername("trainer1", updateRequest))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(username = "john", roles = "TRAINER")
    void shouldForbidTrainerGetAllTrainers_admin_only() {
        assertThatThrownBy(() -> facade.getAllTrainers())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(username = "root", roles = "ADMIN")
    void shouldAllowAdminGetAllTrainers() {
        facade.getAllTrainers();

        assertThatNoException()
                .isThrownBy(() -> facade.getAllTrainers());
    }

    @Test
    void shouldForbidAnonymousCallProtectedMethods() {
        assertThatThrownBy(() -> facade.getAllTrainers())
                .hasMessageContaining("An Authentication object was not found")
                .isInstanceOf(org.springframework.security.authentication.AuthenticationCredentialsNotFoundException.class);
    }

    @Test
    void shouldAllowAnonymousCallUnprotectedMethods() {
        var createRequest = new TraineeCreateRequest();

        facade.addTrainee(createRequest);

        assertThatNoException()
                .isThrownBy(() -> facade.addTrainee(createRequest));
    }
}
