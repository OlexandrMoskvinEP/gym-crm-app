package com.gym.crm.app.controller;

import com.gym.crm.app.api.ChangePasswordApi;
import com.gym.crm.app.api.LoginApi;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.ChangePasswordRequest;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

import static com.gym.crm.app.controller.ApiConstants.ROOT_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(ROOT_PATH + "/login")
public class AuthenticateController implements LoginApi, ChangePasswordApi {
    private final GymFacade facade;
    private final AuthenticationService authenticationService;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return LoginApi.super.getRequest();
    }

    @PostMapping
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest) {
        authenticationService.login(loginRequest);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequest changePasswordRequest) {
        return ChangePasswordApi.super.changePassword(changePasswordRequest);
    }
}
