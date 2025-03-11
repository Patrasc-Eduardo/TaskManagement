package com.rrom.auth_service.controller;

import com.rrom.auth_service.client.KeycloakClient;
import com.rrom.auth_service.dto.request.LoginRequest;
import com.rrom.auth_service.dto.request.SignUpRequest;
import com.rrom.auth_service.dto.response.KeycloakTokenResponse;
import com.rrom.auth_service.dto.response.SignUpResponse;
import com.rrom.auth_service.model.User;
import com.rrom.auth_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    private final KeycloakClient keycloakClient;

    private final UserService userService;

    public AuthController(KeycloakClient keycloakClient, UserService userService) {
        this.keycloakClient = keycloakClient;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            KeycloakTokenResponse tokenResponse = keycloakClient.requestToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials or Keycloak error");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        try {
            User localUser = userService.createUserGlobally(request);

            SignUpResponse resp = new SignUpResponse();
            resp.setMessage("User created successfully");
            resp.setKeycloakUserId(localUser.getKeycloakUserId());
            resp.setLocalUserId(localUser.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(resp);

        } catch (Exception e) {
            SignUpResponse resp = new SignUpResponse();
            resp.setMessage("Sign-up failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }
}
