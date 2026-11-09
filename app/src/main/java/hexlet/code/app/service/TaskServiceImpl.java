package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskParamsDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.specification.TaskSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final UserRepository userRepository;
    private final TaskStatusRepository statusRepository;
    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;

    private final TaskMapper taskMapper;

    private TaskSpecification specBuilder;


    @Override
    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return taskMapper.map(task);
    }

    @Override
    public List<TaskDTO> findAll(TaskParamsDTO params) {
        var spec = specBuilder.build(params);
        return taskRepository.findAll(spec).stream().map(taskMapper::map).toList();
    }

    @Transactional
    @Override
    public TaskDTO createTask(TaskCreateDTO dto) {
        var status = statusRepository.findBySlug(dto.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));

        var task = taskMapper.map(dto);
        task.setTaskStatus(status);

        if (dto.getAssigneeId() != null) {
            var user = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignee(user);
        }

        if (dto.getTaskLabelIds() != null) {
            for (var labelId : dto.getTaskLabelIds()) {
                var label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
                task.addLabel(label);
            }
        }

        return taskMapper.map(taskRepository.save(task));
    }

    @Transactional
    @Override
    public TaskDTO updateTask(Long id, TaskUpdateDTO dto) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskMapper.update(dto, task);

        if (dto.getStatus() != null) {
            var status = statusRepository.findBySlug(dto.getStatus())
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
            task.setTaskStatus(status);
        }

        if (dto.getAssigneeId() != null) {
            var user = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignee(user);
        }

        if (dto.getTaskLabelIds() != null) {
            for (var labelId : dto.getTaskLabelIds()) {
                var label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
                task.addLabel(label);
            }
        }

        return taskMapper.map(taskRepository.save(task));
    }

    @Transactional
    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
