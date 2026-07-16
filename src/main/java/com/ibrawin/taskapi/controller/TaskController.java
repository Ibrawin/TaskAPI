package com.ibrawin.taskapi.controller;

import com.ibrawin.taskapi.model.TaskRequest;
import com.ibrawin.taskapi.model.TaskResponse;
import com.ibrawin.taskapi.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(TaskController.TASK_URL)
public class TaskController {

    public static final String TASK_URL = "/api/tasks";
    public static final String TASK_ID = "/{id}";
    public static final String TASK_ID_URL = TASK_URL + TASK_ID;

    private final TaskService taskService;

    @PostMapping
    ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.saveTask(request);
        return ResponseEntity
                .created(URI.create(TASK_URL + response.id()))
                .body(response);
    }

    @GetMapping
    ResponseEntity<List<TaskResponse>> getAllTasks() {

        return ResponseEntity
                .ok(taskService.getTasks());
    }

    @GetMapping(TASK_ID)
    ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID id) {

        return ResponseEntity
                .ok(taskService.getTaskById(id));
    }

    @PutMapping(TASK_ID)
    ResponseEntity<TaskResponse> updateTaskById(@PathVariable UUID id, @Valid @RequestBody TaskRequest task) {

        return ResponseEntity
                .ok(taskService.updateTaskById(id, task));
    }

    @DeleteMapping(TASK_ID)
    ResponseEntity<Void> deleteTaskById(@PathVariable UUID id) {
        taskService.deleteTaskById(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
