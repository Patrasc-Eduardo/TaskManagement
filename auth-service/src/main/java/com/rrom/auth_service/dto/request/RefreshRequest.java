package com.rrom.auth_service.dto.request;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
