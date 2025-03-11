package com.rrom.auth_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "app_users")
@Getter
@Setter
public class User {
    @Id
    private Long id;

    // The Keycloak user ID or "subject" claim
    @Column(name = "kc_user_id", unique = true, nullable = false)
    private String keycloakUserId;

    // Additional fields you might want
    @Column(nullable = false)
    private String username;

    private String email;
    private String firstName;
    private String lastName;



}
