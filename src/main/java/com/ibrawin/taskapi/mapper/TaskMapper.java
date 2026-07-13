package com.ibrawin.taskapi.mapper;

import com.ibrawin.taskapi.domain.Task;
import com.ibrawin.taskapi.model.TaskRequest;
import com.ibrawin.taskapi.model.TaskResponse;
import org.mapstruct.Mapper;

@Mapper
public interface TaskMapper {

    Task taskRequestToTaskMapper(TaskRequest request);

    TaskResponse taskToTaskResponseMapper(Task task);
}
