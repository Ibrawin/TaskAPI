package com.ibrawin.taskapi.controller;

import com.ibrawin.taskapi.domain.Task;
import com.ibrawin.taskapi.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private static final String taskURL = "/api/task";
    private static final String taskIdURL = taskURL + "/{id}";

    private final TaskService taskService;

    @PostMapping(taskURL)
    ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task newTask = taskService.saveTask(task);
        return ResponseEntity
                .created(URI.create(taskURL + newTask.getId()))
                .body(task);
    }

    @GetMapping(taskURL)
    ResponseEntity<List<Task>> getAllTasks() {

        return ResponseEntity
                .ok(taskService.getTasks());
    }

    @GetMapping(taskIdURL)
    ResponseEntity<Task> getTaskById(@PathVariable UUID id) {

        return ResponseEntity
                .ok(taskService.getTaskById(id));
    }

    @PutMapping(taskIdURL)
    ResponseEntity<Task> updateTaskById(@PathVariable UUID id, @RequestBody Task task) {

        return ResponseEntity
                .ok(taskService.updateTaskById(id, task));
    }

    @DeleteMapping(taskIdURL)
    ResponseEntity<Void> deleteTaskById(@PathVariable UUID id) {
        taskService.deleteTaskById(id);

        return ResponseEntity
                .noContent()
                .build();
    }


}
