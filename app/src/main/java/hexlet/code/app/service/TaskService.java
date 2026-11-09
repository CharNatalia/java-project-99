package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskParamsDTO;
import hexlet.code.app.dto.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    TaskDTO findById(Long id);

    List<TaskDTO> findAll(TaskParamsDTO params);

    TaskDTO createTask(TaskCreateDTO dto);

    TaskDTO updateTask(Long id, TaskUpdateDTO dto);

    void deleteTask(Long id);
}
