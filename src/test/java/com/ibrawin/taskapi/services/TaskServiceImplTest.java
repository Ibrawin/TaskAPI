package com.ibrawin.taskapi.services;

import com.ibrawin.taskapi.domain.Task;
import com.ibrawin.taskapi.exceptions.NotFoundException;
import com.ibrawin.taskapi.mapper.TaskMapper;
import com.ibrawin.taskapi.model.TaskRequest;
import com.ibrawin.taskapi.model.TaskResponse;
import com.ibrawin.taskapi.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;

    private TaskRequest taskRequest;

    private Task task;

    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        taskRequest = new TaskRequest("Clean Flat", "Cleaning the flat thoroughly", false);

        task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle("Clean Flat");
        task.setDescription("Cleaning the flat thoroughly");
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());

        taskResponse = new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted(), task.getCreatedAt());
    }

    @Test
    void saveTask() {
        given(taskMapper.toTask(taskRequest))
                .willReturn(task);
        given(taskMapper.toTaskResponse(task))
                .willReturn(taskResponse);

        TaskResponse response = taskService.saveTask(taskRequest);

        verify(taskMapper).toTask(taskRequest);
        verify(taskRepository).save(taskArgumentCaptor.capture());

        Task savedTask = taskArgumentCaptor.getValue();
        assertThat(savedTask.getCreatedAt()).isNotNull();
        assertThat(response).isEqualTo(taskResponse);
    }

    @Test
    void getTasks() {
        given(taskRepository.findAll())
                .willReturn(List.of(task));

        given(taskMapper.toTaskResponse(task))
                .willReturn(taskResponse);

        List<TaskResponse> responses = taskService.getTasks();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst()).isEqualTo(taskResponse);
    }

    @Test
    void getTasksEmpty() {
        given(taskRepository.findAll())
                .willReturn(List.of());

        List<TaskResponse> responses = taskService.getTasks();

        assertThat(responses).isEmpty();
    }

    @Test
    void getTaskByIdFound() {
        given(taskRepository.findById(taskResponse.id()))
                .willReturn(Optional.of(task));

        given(taskMapper.toTaskResponse(task))
                .willReturn(taskResponse);

        TaskResponse response = taskService.getTaskById(taskResponse.id());

        verify(taskRepository).findById(taskResponse.id());
        assertThat(response).isEqualTo(taskResponse);
    }

    @Test
    void getTaskByIdNotFound() {
        given(taskRepository.findById(any(UUID.class)))
                .willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.getTaskById(UUID.randomUUID()));
    }

    @Test
    void updateTaskByIdFound() {
        Task newTask = new Task();
        newTask.setTitle("Hello world");

        given(taskRepository.findById(task.getId()))
                .willReturn(Optional.of(task));
        given(taskMapper.toTask(taskRequest))
                .willReturn(newTask);
        given(taskRepository.save(newTask))
                .willReturn(newTask);
        given(taskMapper.toTaskResponse(newTask))
                .willReturn(taskResponse);

        TaskResponse response = taskService.updateTaskById(task.getId(), taskRequest);

        verify(taskRepository).save(taskArgumentCaptor.capture());

        assertThat(taskArgumentCaptor.getValue().getCreatedAt()).isEqualTo(task.getCreatedAt());
        assertThat(taskArgumentCaptor.getValue().getId()).isEqualTo(task.getId());

    }

    @Test
    void updateTasksByIdNotFound() {
        given(taskRepository.findById(any(UUID.class)))
                .willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.updateTaskById(UUID.randomUUID(), taskRequest));
    }

    @Test
    void deleteTaskByIdFound() {

        given(taskRepository.existsById(task.getId()))
                .willReturn(true);

        taskService.deleteTaskById(task.getId());

        verify(taskRepository).deleteById(task.getId());
    }

    @Test
    void deleteTaskByIdNotFound() {
        given(taskRepository.existsById(any(UUID.class)))
                .willReturn(false);

        assertThrows(NotFoundException.class, () -> taskService.deleteTaskById(UUID.randomUUID()));

        verify(taskRepository, never()).deleteById(any());
    }
}