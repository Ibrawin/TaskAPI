package com.ibrawin.taskapi.controller;

import com.ibrawin.taskapi.exceptions.NotFoundException;
import com.ibrawin.taskapi.model.TaskRequest;
import com.ibrawin.taskapi.model.TaskResponse;
import com.ibrawin.taskapi.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @MockitoBean
    TaskService taskService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<TaskRequest> taskRequestArgumentCaptor;

    private TaskRequest taskRequest;

    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        taskRequest = new TaskRequest("Clean Flat", "Cleaning the flat thoroughly", false);
        taskResponse = new TaskResponse(UUID.randomUUID(), "Clean Flat", "Cleaning the flat thoroughly", false, LocalDateTime.now());
    }

    @Test
    void createTask() throws Exception {


        given(taskService.saveTask(any(TaskRequest.class)))
                .willReturn(taskResponse);

        mockMvc.perform(post(TaskController.TASK_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists());

        verify(taskService).saveTask(taskRequestArgumentCaptor.capture());

        assertThat(taskRequestArgumentCaptor.getValue().title()).isEqualTo(taskRequest.title());
    }

    @Test
    void getAllTasks() throws Exception {

        given(taskService.getTasks())
                .willReturn(List.of(
                        taskResponse,
                        new TaskResponse(UUID.randomUUID(), "Clean House", "Fleaning the house thoroughly", false, LocalDateTime.now())));

        mockMvc.perform(get(TaskController.TASK_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    void getEmptyTasks() throws Exception {

        given(taskService.getTasks())
                .willReturn(Collections.emptyList());

        mockMvc.perform(get(TaskController.TASK_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void getTaskByIdFound() throws Exception {

        given(taskService.getTaskById(any(UUID.class)))
                .willReturn(taskResponse);

        mockMvc.perform(get(TaskController.TASK_ID_URL, taskResponse.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(taskResponse.id().toString())));

        verify(taskService).getTaskById(uuidArgumentCaptor.capture());
        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(taskResponse.id());

    }

    @Test
    void getTaskByIdNotFound() throws Exception {

        given(taskService.getTaskById(any(UUID.class)))
                .willThrow(NotFoundException.class);

        mockMvc.perform(get(TaskController.TASK_ID_URL, UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTaskById() {
    }

    @Test
    void deleteTaskById() {
    }
}