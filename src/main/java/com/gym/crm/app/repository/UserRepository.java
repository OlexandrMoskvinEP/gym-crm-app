package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Transactional
    @Modifying
    void deleteByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.username = :username")
    void updatePassword(@Param("username") String username, @Param("password") String newPassword);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isActive = CASE WHEN u.isActive = true THEN false ELSE true END WHERE u.username = :username")
    void changeStatus(@Param("username") String username);
}
