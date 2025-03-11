package com.rrom.auth_service.client;

import com.rrom.auth_service.dto.request.SignUpRequest;
import com.rrom.auth_service.dto.response.KeycloakTokenResponse;
import com.rrom.auth_service.error.KeycloakException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KeycloakClient {

    @Value("${keycloak.host}")
    private String keycloakHost;

    @Value("${keycloak.port}")
    private String keycloakPort;

    @Value("${keycloak.realm}")
    private String realm;

    // The realm used for admin credentials, often "master" or the same realm if you prefer
    @Value("${keycloak.adminRealm:master}")
    private String adminRealm;

    // If you prefer to authenticate as "admin" user with password or a service account
    @Value("${keycloak.adminUser:admin}")
    private String adminUser;

    @Value("${keycloak.adminPass:admin}")
    private String adminPass;

    // If using "admin-cli" or a custom client with realm-management roles
    @Value("${keycloak.adminClientId:admin-cli}")
    private String adminClientId;

    @Value("${keycloak.clientId}")
    private String clientId;

    @Value("${keycloak.clientSecret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public KeycloakTokenResponse requestToken(String username, String password) throws IOException {
        // Endpoint de token Keycloak
        String tokenUrl = String.format("http://%s:%s/realms/%s/protocol/openid-connect/token",
                keycloakHost, keycloakPort, realm);

        // Construim corpul request-ului
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "password");
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("username", username);
        body.put("password", password);

        // HEADERS
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Convert map -> form data
        String formData = body.entrySet().stream()
                .map(e -> e.getKey() + "=" + UriUtils.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpEntity<String> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<KeycloakTokenResponse> response = restTemplate
                .exchange(tokenUrl, HttpMethod.POST, requestEntity, KeycloakTokenResponse.class);

        return response.getBody();
    }

    /**
     * Create a new user in Keycloak and return the Keycloak user ID.
     */
    public String createUserInKeycloak(SignUpRequest signUpRequest) throws IOException {
        String adminToken = getAdminAccessToken();

        // Build user payload
        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("username", signUpRequest.getUsername());
        userPayload.put("enabled", true);
        userPayload.put("firstName", signUpRequest.getFirstName());
        userPayload.put("lastName", signUpRequest.getLastName());
        userPayload.put("email", signUpRequest.getEmail());

        // Add password credentials
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", signUpRequest.getPassword());
        credentials.put("temporary", false);

        userPayload.put("credentials", List.of(credentials));

        // POST to /admin/realms/{realm}/users
        String url = String.format("http://%s:%s/admin/realms/%s/users",
                keycloakHost, keycloakPort, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userPayload, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, request, String.class
        );

        if (response.getStatusCode() != HttpStatus.CREATED
                && !response.getStatusCode().is2xxSuccessful()) {
            throw new KeycloakException("Failed to create user in Keycloak: " + response.getBody());
        }

        URI location = response.getHeaders().getLocation();
        if (location == null) {
            throw new KeycloakException("Keycloak user created but no 'Location' header returned.");
        }

        // Extract user ID from location
        String locationStr = location.toString();
        String keycloakUserId = locationStr.substring(locationStr.lastIndexOf('/') + 1);
        log.info("Created user in Keycloak with ID: {}", keycloakUserId);

        return keycloakUserId;
    }

    /**
     * Delete a user from Keycloak by user ID.
     */
    public void deleteUserFromKeycloak(String keycloakUserId) {
        try {
            String adminToken = getAdminAccessToken();

            String url = String.format("http://%s:%s/admin/realms/%s/users/%s",
                    keycloakHost, keycloakPort, realm, keycloakUserId);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, request, String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Failed to delete user from Keycloak: status={}, body={}",
                        response.getStatusCode(), response.getBody());
            } else {
                log.info("Deleted Keycloak user with ID: {}", keycloakUserId);
            }
        } catch (Exception e) {
            log.error("Error deleting user from Keycloak: {}", e.getMessage(), e);
        }
    }

    /**
     * Obtain an admin token from Keycloak (e.g., master realm) to manage users.
     */
    private String getAdminAccessToken() throws IOException {
        String tokenUrl = String.format("http://%s:%s/realms/%s/protocol/openid-connect/token",
                keycloakHost, keycloakPort, adminRealm);

        // Using the "admin-cli" approach with username/password
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", adminClientId);
        formData.add("username", adminUser);
        formData.add("password", adminPass);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
        ResponseEntity<KeycloakTokenResponse> response = restTemplate.exchange(
                tokenUrl, HttpMethod.POST, requestEntity, KeycloakTokenResponse.class
        );

        KeycloakTokenResponse tokenResp = response.getBody();
        if (tokenResp == null || tokenResp.getAccessToken() == null) {
            throw new KeycloakException("Failed to obtain admin token from Keycloak");
        }

        return tokenResp.getAccessToken();
    }
}

