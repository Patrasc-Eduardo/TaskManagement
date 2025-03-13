package com.rrom.task_management.repository;

import com.rrom.task_management.model.Task;
import com.rrom.task_management.model.TaskStatus;
import com.rrom.task_management.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskRepositoryUnitTest {

    @Mock
    private TaskRepository mockRepo;

    private User userOwner;
    private User userOther;
    private Pageable pageable;

    @BeforeEach
    void setup() {
        userOwner = new User();
        userOwner.setId(1L);
        userOwner.setUsername("ownerUser");

        userOther = new User();
        userOther.setId(2L);
        userOther.setUsername("otherUser");

        pageable = PageRequest.of(0, 5, Sort.by("id"));
    }

    @Test
    void findAllAccessibleByUser_ReturnsMockedPage() {
        Task t1 = new Task();
        t1.setId(101L);
        t1.setTitle("Mocked Task 1");
        t1.setOwner(userOwner);
        t1.setStatus(TaskStatus.TODO);

        Page<Task> mockPage = new PageImpl<>(List.of(t1));
        when(mockRepo.findAllAccessibleByUser(userOwner, pageable)).thenReturn(mockPage);

        Page<Task> result = mockRepo.findAllAccessibleByUser(userOwner, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Mocked Task 1", result.getContent().getFirst().getTitle());
        verify(mockRepo).findAllAccessibleByUser(userOwner, pageable);
    }

    @Test
    void findAllAccessibleByUser_EmptyPage() {
        Page<Task> emptyPage = Page.empty();
        when(mockRepo.findAllAccessibleByUser(userOther, pageable)).thenReturn(emptyPage);

        Page<Task> result = mockRepo.findAllAccessibleByUser(userOther, pageable);

        assertTrue(result.isEmpty());
        verify(mockRepo).findAllAccessibleByUser(userOther, pageable);
    }

    @Test
    void findByIdAndUserAccessible_Found() {
        Task t2 = new Task();
        t2.setId(202L);
        t2.setTitle("Shared Task");
        t2.setOwner(userOwner);

        when(mockRepo.findByIdAndUserAccessible(202L, userOwner)).thenReturn(Optional.of(t2));

        Optional<Task> result = mockRepo.findByIdAndUserAccessible(202L, userOwner);
        assertTrue(result.isPresent());
        assertEquals("Shared Task", result.get().getTitle());
        verify(mockRepo).findByIdAndUserAccessible(202L, userOwner);
    }

    @Test
    void findByIdAndUserAccessible_NotFound() {
        when(mockRepo.findByIdAndUserAccessible(999L, userOther)).thenReturn(Optional.empty());

        Optional<Task> result = mockRepo.findByIdAndUserAccessible(999L, userOther);
        assertTrue(result.isEmpty());
        verify(mockRepo).findByIdAndUserAccessible(999L, userOther);
    }
}
