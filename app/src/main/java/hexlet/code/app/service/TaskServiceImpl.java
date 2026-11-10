package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskParamsDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
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
        var task = taskMapper.map(dto);
        setRelations(task, dto.getStatus(), dto.getAssigneeId(), dto.getTaskLabelIds());
        return taskMapper.map(taskRepository.save(task));
    }

    @Transactional
    @Override
    public TaskDTO updateTask(Long id, TaskUpdateDTO dto) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskMapper.update(dto, task);

        setRelations(task, dto.getStatus(), dto.getAssigneeId(), dto.getTaskLabelIds());

        return taskMapper.map(taskRepository.save(task));
    }

    @Transactional
    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    private void setRelations(Task task, String slug, Long userId, List<Long> labelIds) {
        if (slug != null) {
            var status = statusRepository.findBySlug(slug)
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
            task.setTaskStatus(status);
        }

        if (userId != null) {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignee(user);
        }

        if (labelIds != null) {
            for (var labelId : labelIds) {
                var label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
                task.addLabel(label);
            }
        }
    }
}
