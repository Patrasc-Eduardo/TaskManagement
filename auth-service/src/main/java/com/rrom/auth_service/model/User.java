package com.rrom.auth_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kc_user_id", unique = true, nullable = false)
    private String keycloakUserId;

    @Column(nullable = false)
    private String username;

    private String email;
    private String firstName;
    private String lastName;



}
