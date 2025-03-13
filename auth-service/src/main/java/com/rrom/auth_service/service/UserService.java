package com.rrom.auth_service.service;

import com.rrom.auth_service.client.KeycloakClient;
import com.rrom.auth_service.dto.request.SignUpRequest;
import com.rrom.auth_service.error.KeycloakException;
import com.rrom.auth_service.model.User;
import com.rrom.auth_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Slf4j
public class UserService {

    private final KeycloakClient keycloakClient;
    private final UserRepository userRepository;

    public UserService(KeycloakClient keycloakClient, UserRepository userRepository) {
        this.keycloakClient = keycloakClient;
        this.userRepository = userRepository;
    }

    /**
     * Creates a user in Keycloak and in the local DB.
     * If DB creation fails, remove user from Keycloak (compensation).
     */
    @Transactional
    public User createUserGlobally(SignUpRequest signUpRequest) {
        String keycloakUserId;

        try {
            keycloakUserId = keycloakClient.createUserInKeycloak(signUpRequest);
        } catch (IOException e) {
            throw new KeycloakException("Keycloak creation failed: " + e.getMessage(), e);
        }

        User localUser = new User();
        localUser.setKeycloakUserId(keycloakUserId);
        localUser.setUsername(signUpRequest.getUsername());
        localUser.setEmail(signUpRequest.getEmail());
        localUser.setFirstName(signUpRequest.getFirstName());
        localUser.setLastName(signUpRequest.getLastName());

        try {
            localUser = userRepository.save(localUser);
            log.info("User saved in local DB with ID: {}", localUser.getId());
        } catch (Exception dbException) {
            log.error("DB save failed, rolling back Keycloak user: {}", keycloakUserId);
            keycloakClient.deleteUserFromKeycloak(keycloakUserId);
            throw new RuntimeException("Failed to save user in DB, rolled back Keycloak user.", dbException);
        }

        return localUser;
    }
}
