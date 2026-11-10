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
public class TaskStatusServiceImpl implements TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    private static final String STATUS_NOT_FOUND = "Status not found";


    @Override
    public TaskStatusDTO findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND));
        return taskStatusMapper.map(taskStatus);
    }

    @Override
    public List<TaskStatusDTO> findAll() {
        return taskStatusRepository.findAll().stream().map(taskStatusMapper::map).toList();
    }

    @Transactional
    @Override
    public TaskStatusDTO createStatus(TaskStatusCreateDTO dto) {
        var taskStatus = taskStatusMapper.map(dto);
        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    @Override
    public TaskStatusDTO updateStatus(Long id, @Valid TaskStatusUpdateDTO dto) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND));
        taskStatusMapper.update(dto, taskStatus);
        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    @Override
    public void deleteStatus(Long id) {
        taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STATUS_NOT_FOUND));
        taskStatusRepository.deleteById(id);
    }
}
