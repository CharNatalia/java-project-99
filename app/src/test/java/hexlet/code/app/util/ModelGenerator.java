package hexlet.code.app.util;

import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModelGenerator {
    @Autowired
    private Faker faker;

    public User createUser() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .set(Select.field(User::getEmail), faker.internet().emailAddress())
                .create();
    }

    public TaskStatus createTaskStatus() {
        return Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .set(Select.field(TaskStatus::getSlug), faker.internet().slug())
                .set(Select.field(TaskStatus::getName), faker.name().title())
                .create();
    }

    public Task createTask() {
        return Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getCreatedAt))
                .set(Select.field(Task::getName), faker.name().title())
                .set(Select.field(Task::getTaskStatus), createTaskStatus())
                .create();
    }
}
