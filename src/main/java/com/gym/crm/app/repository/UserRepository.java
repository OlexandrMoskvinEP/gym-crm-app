package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findByUsername(String username);

    void update(User user);

    void deleteByUsername(String username);

     List<User> findAll();

    void updatePassword(String username, String newPassword);

    void changeStatus(String username);
}
