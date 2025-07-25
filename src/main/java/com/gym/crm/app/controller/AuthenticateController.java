package com.gym.crm.app.controller;

import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.ChangePasswordRequest;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gym.crm.app.controller.ApiConstants.ROOT_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ROOT_PATH)
public class AuthenticateController {
    private final GymFacade facade;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt: username={}", loginRequest.getUsername());

        authenticationService.login(loginRequest);

        log.info("User successfully authenticated: username={}", loginRequest.getUsername());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        log.info("Change password attempt: username={}", changePasswordRequest.getUsername());

        facade.changePassword(changePasswordRequest);

        log.info("Password successfully changed: username={}", changePasswordRequest.getUsername());

        return ResponseEntity.ok().build();
    }
}
