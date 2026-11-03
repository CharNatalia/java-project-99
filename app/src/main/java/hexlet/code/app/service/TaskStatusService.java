package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    public TaskStatusDTO findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Статус не найден"));
        return taskStatusMapper.map(taskStatus);
    }

    public List<TaskStatusDTO> findAll() {
        return taskStatusRepository.findAll().stream().map(taskStatusMapper::map).toList();
    }

    @Transactional
    public TaskStatusDTO createStatus(TaskStatusCreateDTO dto) {
        var taskStatus = taskStatusMapper.map(dto);
        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    public TaskStatusDTO updateStatus(Long id, @Valid TaskStatusUpdateDTO dto) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Статус не найден"));
        taskStatusMapper.update(dto, taskStatus);
        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    public void deleteStatus(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
