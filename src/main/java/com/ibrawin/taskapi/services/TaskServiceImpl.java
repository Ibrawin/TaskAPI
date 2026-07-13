package com.ibrawin.taskapi.services;

import com.ibrawin.taskapi.domain.Task;
import com.ibrawin.taskapi.mapper.TaskMapper;
import com.ibrawin.taskapi.model.TaskRequest;
import com.ibrawin.taskapi.model.TaskResponse;
import com.ibrawin.taskapi.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse saveTask(TaskRequest request) {
        Task task = taskMapper.taskRequestToTaskMapper(request);
        task.setCreatedAt(LocalDateTime.now());
        taskRepository.save(task);
        return taskMapper.taskToTaskResponseMapper(task);
    }

    @Override
    public List<TaskResponse> getTasks() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::taskToTaskResponseMapper)
                .toList();
    }

    @Override
    public TaskResponse getTaskById(UUID id) {
        return taskRepository.findById(id)
                .stream()
                .map(taskMapper::taskToTaskResponseMapper)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public TaskResponse updateTaskById(UUID id, TaskRequest request) {
        Optional<Task> oldTask = taskRepository.findById(id);
        if (oldTask.isPresent()) {
            Task newTask = taskMapper.taskRequestToTaskMapper(request);
            newTask.setId(id);
            newTask.setCreatedAt(oldTask.get().getCreatedAt());
            return taskMapper.taskToTaskResponseMapper(taskRepository.save(newTask));
        }
        throw new RuntimeException();
    }

    @Override
    public void deleteTaskById(UUID id) {
        taskRepository.deleteById(id);
    }
}
