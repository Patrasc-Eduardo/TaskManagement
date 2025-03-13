package com.rrom.task_management.repository;

import com.rrom.task_management.model.Task;
import com.rrom.task_management.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TaskRepositoryIT {
    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private UserRepository userRepo;

    private User owner;
    private User other;

    @BeforeEach
    void setup() {
        taskRepo.deleteAll();
        userRepo.deleteAll();

        owner = new User();
        owner.setUsername("ownerUser");
        owner.setKeycloakUserId("kc-123");
        userRepo.save(owner);

        other = new User();
        other.setUsername("otherUser");
        other.setKeycloakUserId("kc-1234");
        userRepo.save(other);

        Task t1 = new Task();
        t1.setTitle("Owner Task 1");
        t1.setOwner(owner);
        taskRepo.save(t1);

        Task t2 = new Task();
        t2.setTitle("Shared Task");
        t2.setOwner(owner);
        t2.setSharedWithUsers(Set.of(other));
        taskRepo.save(t2);

        Task t3 = new Task();
        t3.setTitle("Other's Task");
        t3.setOwner(other);
        taskRepo.save(t3);
    }

    @Test
    void findAllAccessibleByUser_Owner() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Task> page = taskRepo.findAllAccessibleByUser(owner, pageable);

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream()
                .anyMatch(t -> "Owner Task 1".equals(t.getTitle())));
        assertTrue(page.getContent().stream()
                .anyMatch(t -> "Shared Task".equals(t.getTitle())));
    }

    @Test
    void findAllAccessibleByUser_Other() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Task> page = taskRepo.findAllAccessibleByUser(other, pageable);

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream()
                .anyMatch(t -> "Other's Task".equals(t.getTitle())));
        assertTrue(page.getContent().stream()
                .anyMatch(t -> "Shared Task".equals(t.getTitle())));
    }

    @Test
    void findByIdAndUserAccessible_Found() {
        Task shared = taskRepo.findAll().stream()
                .filter(t -> "Shared Task".equals(t.getTitle()))
                .findFirst().orElseThrow();

        Optional<Task> result = taskRepo.findByIdAndUserAccessible(shared.getId(), other);
        assertTrue(result.isPresent());
        assertEquals("Shared Task", result.get().getTitle());
    }

    @Test
    void findByIdAndUserAccessible_NotFound() {
        Task ownersTask = taskRepo.findAll().stream()
                .filter(t -> "Owner Task 1".equals(t.getTitle()))
                .findFirst().orElseThrow();

        Optional<Task> result = taskRepo.findByIdAndUserAccessible(ownersTask.getId(), other);
        assertTrue(result.isEmpty());
    }
}
