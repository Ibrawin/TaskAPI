package com.ibrawin.taskapi.services;

import com.ibrawin.taskapi.model.TaskRequest;
import com.ibrawin.taskapi.model.TaskResponse;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    TaskResponse saveTask(TaskRequest task);

    List<TaskResponse> getTasks();

    TaskResponse getTaskById(UUID id);

    TaskResponse updateTaskById(UUID id, TaskRequest task);

    void deleteTaskById(UUID id);
}
