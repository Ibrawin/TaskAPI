package com.ibrawin.taskapi.services;

import com.ibrawin.taskapi.domain.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    Task saveTask(Task task);

    List<Task> getTasks();

    Task getTaskById(UUID id);

    Task updateTaskById(UUID id, Task task);

    void deleteTaskById(UUID id);
}
