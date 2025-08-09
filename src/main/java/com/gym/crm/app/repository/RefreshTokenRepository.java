package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    @Transactional
    void deleteByUserId(Long userId);
}
