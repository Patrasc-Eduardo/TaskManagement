package com.rrom.task_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasksOwned = new HashSet<>();

    @ManyToMany(mappedBy = "sharedWithUsers")
    private Set<Task> tasksSharedWithMe = new HashSet<>();


}
