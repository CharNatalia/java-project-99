package hexlet.code.app.controller.api;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
@AllArgsConstructor
public class TaskStatusesController {
    private final TaskStatusService taskStatusService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> show(@PathVariable Long id) {
        return ResponseEntity.ok(taskStatusService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskStatusDTO>> index() {
        var taskStatuses = taskStatusService.findAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatuses.size()))
                .body(taskStatuses);
    }

    @PostMapping
    public ResponseEntity<TaskStatusDTO> create(@Valid @RequestBody TaskStatusCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskStatusService.createStatus(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> update(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDTO dto) {
        return ResponseEntity.ok().body(taskStatusService.updateStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskStatusService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }
}
