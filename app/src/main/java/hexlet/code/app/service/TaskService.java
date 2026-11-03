package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {
    private final UserRepository userRepository;
    private final TaskStatusRepository statusRepository;
    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;


    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача не найдена"));
        return taskMapper.map(task);
    }

    public List<TaskDTO> findAll() {
        return taskRepository.findAll().stream().map(taskMapper::map).toList();
    }

    @Transactional
    public TaskDTO createTask(TaskCreateDTO dto) {
        var status = statusRepository.findBySlug(dto.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("Статус не найден"));

        var task = taskMapper.map(dto);
        task.setTaskStatus(status);

        if (dto.getAssigneeId() != null) {
            var user = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
            task.setAssignee(user);
        }

        return taskMapper.map(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskUpdateDTO dto) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задача не найдена"));
        taskMapper.update(dto, task);

        if (dto.getStatus() != null) {
            var status = statusRepository.findBySlug(dto.getStatus())
                    .orElseThrow(() -> new ResourceNotFoundException("Статус не найден"));
            task.setTaskStatus(status);
        }

        if (dto.getAssigneeId() != null) {
            var user = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
            task.setAssignee(user);
        }

        return taskMapper.map(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
