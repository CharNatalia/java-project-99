package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface TaskStatusService {
    TaskStatusDTO findById(Long id);

    List<TaskStatusDTO> findAll();

    TaskStatusDTO createStatus(TaskStatusCreateDTO dto);

    TaskStatusDTO updateStatus(Long id, @Valid TaskStatusUpdateDTO dto);

    void deleteStatus(Long id);
}
