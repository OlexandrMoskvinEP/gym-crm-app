package com.gym.crm.app.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.app.domain.model.RefreshToken;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataSet(value = {"datasets/users.xml", "datasets/refresh_tokens.xml"}, cleanBefore = true, cleanAfter = true)
class RefreshTokenRepositoryTest extends AbstractRepositoryTest<RefreshTokenRepository> {

    @Test
    void shouldDeleteTokenByUserId() {
        repository.deleteByUserId(6L);

        var token = repository.findById("schwarzToken");

        assertThat(token).isNotPresent();
    }

    @Test
    void shouldAddTokenSuccessfully() {
        repository.save(buildNewRefreshToken());

        var token = repository.findById("someVerySecretToken");

        assertThat(token).isPresent();
        assertThat(token.get().getExpiresAt()).isBefore(Instant.now().plus(15, ChronoUnit.DAYS));
        assertThat(token.get().getIssuedAt()).isAfter(Instant.now().minusMillis(1000));
    }

    @Test
    void shouldFindTokenById() {
        var token = repository.findById("token111");

        assertThat(token).isPresent();
        assertThat(token.get().getUserId()).isEqualTo(1L);
    }

    private RefreshToken buildNewRefreshToken() {
        return RefreshToken.builder()
                .token("someVerySecretToken")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(14, ChronoUnit.DAYS))
                .userId(4L)
                .build();
    }
}