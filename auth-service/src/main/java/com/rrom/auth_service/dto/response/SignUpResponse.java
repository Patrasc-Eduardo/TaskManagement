package com.rrom.auth_service.dto.response;

import lombok.Data;

@Data
public class SignUpResponse {
    private String message;
    private String keycloakUserId;
    private Long localUserId;
}
