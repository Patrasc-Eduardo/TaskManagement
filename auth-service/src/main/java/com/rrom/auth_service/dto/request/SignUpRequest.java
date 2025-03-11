package com.rrom.auth_service.dto.request;

import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String password;
    private String email;      // optional
    private String firstName;  // optional
    private String lastName;   // optional
}
