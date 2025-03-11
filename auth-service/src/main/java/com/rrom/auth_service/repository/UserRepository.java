package com.rrom.auth_service.repository;

import com.rrom.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakUserId(String kcUserId);
    Optional<User> findByUsername(String username);
}
