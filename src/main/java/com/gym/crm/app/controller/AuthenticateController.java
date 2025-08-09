package com.gym.crm.app.controller;

import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.ChangePasswordRequest;
import com.gym.crm.app.rest.ErrorResponse;
import com.gym.crm.app.rest.JwtTokenResponse;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.rest.RefreshRequest;
import com.gym.crm.app.service.TokenService;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
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
    private final MeterRegistry meterRegistry;
    private final TokenService tokenService;

    @PostConstruct
    public void init() {
        meterRegistry.counter("checkAuthorities.success.count");
    }

    @Operation(
            summary = "User checkAuthorities",
            description = "Authenticates user with username and password",
            tags = {"Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    description = "Unexpected error"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt: username={}", loginRequest.getUsername());

        JwtTokenResponse tokens = tokenService.login(loginRequest);

        log.info("User successfully authenticated: username={}", loginRequest.getUsername());
        meterRegistry.counter("checkAuthorities.success.count").increment();

        return ResponseEntity.ok(tokens);
    }

    @Operation(
            operationId = "refresh",
            summary = "Refresh JWT access token",
            description = "Generates a new access token using a valid refresh token",
            tags = {"Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = JwtTokenResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(description = "Unexpected error")
    })
    @PostMapping("/refresh")
    public ResponseEntity<JwtTokenResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        JwtTokenResponse tokens = tokenService.refresh(req.getRefreshToken());
        meterRegistry.counter("auth.refresh.success.count").increment();

        return ResponseEntity.ok(tokens);
    }

    @Operation(
            operationId = "logout",
            summary = "Logout user",
            description = "Invalidates the provided refresh token",
            tags = {"Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(description = "Unexpected error")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest refreshRequest) {
        tokenService.logout(refreshRequest.getRefreshToken());
        meterRegistry.counter("auth.logout.success.count").increment();

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Change user password",
            description = "Changes user password with old password verification",
            tags = {"Profile services"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid old password",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ), @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    description = "Unexpected error"
            )
    })
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        log.info("Change password attempt: username={}", changePasswordRequest.getUsername());
        facade.changePassword(changePasswordRequest);
        log.info("Password successfully changed: username={}", changePasswordRequest.getUsername());

        return ResponseEntity.ok().build();
    }
}
