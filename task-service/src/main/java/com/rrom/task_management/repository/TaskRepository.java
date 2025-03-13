package com.rrom.task_management.repository;

import com.rrom.task_management.model.Task;
import com.rrom.task_management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
       SELECT t 
         FROM Task t
        WHERE t.owner = :owner
           OR :owner MEMBER OF t.sharedWithUsers
       """)
    Page<Task> findAllAccessibleByUser(User owner, Pageable pageable);

    @Query("""
       SELECT t 
         FROM Task t
        WHERE t.id = :taskId
          AND (t.owner = :owner OR :owner MEMBER OF t.sharedWithUsers)
       """)
    Optional<Task> findByIdAndUserAccessible(Long taskId, User owner);
}
