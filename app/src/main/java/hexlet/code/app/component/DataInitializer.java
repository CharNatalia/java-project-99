package hexlet.code.app.component;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.LabelService;
import hexlet.code.app.service.TaskStatusService;
import hexlet.code.app.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final UserRepository userRepository;
    private final TaskStatusService taskStatusService;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;
    private final LabelService labelService;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            var userData = new UserCreateDTO();
            userData.setEmail("hexlet@example.com");
            userData.setPassword("qwerty");
            userService.createUser(userData);
        }

        createDefaultStatus("Draft", "draft");
        createDefaultStatus("To review", "to_review");
        createDefaultStatus("To be fixed", "to_be_fixed");
        createDefaultStatus("To publish", "to_publish");
        createDefaultStatus("Published", "published");

        createDefaultLabel("feature");
        createDefaultLabel("bug");
    }

    private void createDefaultStatus(String name, String slug) {
        if (taskStatusRepository.findBySlug(slug).isEmpty()) {
            var taskStatus = new TaskStatusCreateDTO();
            taskStatus.setName(name);
            taskStatus.setSlug(slug);
            taskStatusService.createStatus(taskStatus);
        }
    }

    private void createDefaultLabel(String name) {
        if (labelRepository.findByName(name).isEmpty()) {
            var label = new LabelCreateDTO();
            label.setName(name);
            labelService.createLabel(label);
        }
    }
}
