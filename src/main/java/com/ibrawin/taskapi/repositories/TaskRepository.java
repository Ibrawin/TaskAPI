package com.ibrawin.taskapi.repositories;

import com.ibrawin.taskapi.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    UUID id(UUID id);
}
