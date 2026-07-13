package com.ibrawin.taskapi.services;

import com.ibrawin.taskapi.domain.Task;
import com.ibrawin.taskapi.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;


    @Override
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task getTaskById(UUID id) {
        return taskRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public Task updateTaskById(UUID id, Task task) {
        Optional<Task> oldTask = taskRepository.findById(id);
        if (oldTask.isPresent()) {
            task.setId(id);
            return taskRepository.save(task);
        }
        throw new RuntimeException();
    }

    @Override
    public void deleteTaskById(UUID id) {
        taskRepository.deleteById(id);
    }
}
