package com.ibrawin.taskapi.services;

import com.ibrawin.taskapi.domain.Task;
import com.ibrawin.taskapi.exceptions.NotFoundException;
import com.ibrawin.taskapi.mapper.TaskMapper;
import com.ibrawin.taskapi.model.TaskRequest;
import com.ibrawin.taskapi.model.TaskResponse;
import com.ibrawin.taskapi.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public TaskResponse saveTask(TaskRequest request) {
        Task task = taskMapper.toTask(request);
        task.setCreatedAt(LocalDateTime.now());
        taskRepository.save(task);
        return taskMapper.toTaskResponse(task);
    }

    @Override
    public List<TaskResponse> getTasks() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toTaskResponse)
                .toList();
    }

    @Override
    public TaskResponse getTaskById(UUID id) {
        return taskRepository.findById(id)
                .map(taskMapper::toTaskResponse)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskById(UUID id, TaskRequest request) {
        Optional<Task> oldTask = taskRepository.findById(id);
        if (oldTask.isPresent()) {
            Task newTask = taskMapper.toTask(request);
            newTask.setId(id);
            newTask.setCreatedAt(oldTask.get().getCreatedAt());
            return taskMapper.toTaskResponse(taskRepository.save(newTask));
        }
        throw new NotFoundException();
    }

    @Override
    @Transactional
    public void deleteTaskById(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException();
        }

        taskRepository.deleteById(id);
    }
}
