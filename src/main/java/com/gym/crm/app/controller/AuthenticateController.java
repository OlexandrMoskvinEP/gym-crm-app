package com.gym.crm.app.controller;

import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.ChangePasswordRequest;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gym.crm.app.controller.ApiConstants.ROOT_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(ROOT_PATH)
public class AuthenticateController {
    private final GymFacade facade;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest) {
        authenticationService.login(loginRequest);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        facade.changePassword(changePasswordRequest);

        return ResponseEntity.ok().build();
    }
}
