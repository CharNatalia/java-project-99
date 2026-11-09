package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.exception.ResourceAlreadyExistsException;
import hexlet.code.app.exception.ResourceInUseException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    private final TaskRepository taskRepository;

    @Override
    public TaskStatusDTO findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Статус не найден"));
        return taskStatusMapper.map(taskStatus);
    }

    @Override
    public List<TaskStatusDTO> findAll() {
        return taskStatusRepository.findAll().stream().map(taskStatusMapper::map).toList();
    }

    @Transactional
    @Override
    public TaskStatusDTO createStatus(TaskStatusCreateDTO dto) {
        if (dto.getSlug() != null && taskStatusRepository.findBySlug(dto.getSlug()).isPresent()) {
            throw new ResourceAlreadyExistsException("Slug already exists");
        }
        var taskStatus = taskStatusMapper.map(dto);
        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    @Override
    public TaskStatusDTO updateStatus(Long id, @Valid TaskStatusUpdateDTO dto) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Статус не найден"));

        if (dto.getSlug() != null) {
            taskStatusRepository.findBySlug(dto.getSlug())
                    .filter(status -> !Objects.equals(status.getId(), taskStatus.getId()))
                    .ifPresent(status -> {
                        throw new ResourceAlreadyExistsException("Slug already exists");
                    });
        }
        taskStatusMapper.update(dto, taskStatus);
        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    @Override
    public void deleteStatus(Long id) {
        var status = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Статус не найден"));
        if (taskRepository.existsByTaskStatusId(status.getId())) {
            throw new ResourceInUseException("Нельзя удалить статус, который используется в задаче");
        }
        taskStatusRepository.deleteById(id);
    }
}
