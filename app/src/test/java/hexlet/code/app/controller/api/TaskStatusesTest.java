package hexlet.code.app.controller.api;

import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusesTest {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private MockMvc mockMvc;

    private TaskStatus testTaskStatus;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @BeforeEach
    public void setUp() {
        taskStatusRepository.deleteAll();

        testTaskStatus = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .set(Select.field(TaskStatus::getSlug), faker.internet().slug())
                .set(Select.field(TaskStatus::getName), faker.name().title())
                .create();
        taskStatusRepository.save(testTaskStatus);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .set(Select.field(TaskStatus::getSlug), faker.internet().slug())
                .set(Select.field(TaskStatus::getName), faker.name().title())
                .create();
        var request =
                post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isCreated());

        var taskStatus = taskStatusRepository.findBySlug(data.getSlug()).orElse(null);

        assertNotNull(taskStatus);
        assertThat(taskStatus.getSlug()).isEqualTo(data.getSlug());
        assertThat(taskStatus.getName()).isEqualTo(data.getName());
    }

    @Test
    public void testIndex() throws Exception {
        var response =
                mockMvc.perform(get("/api/task_statuses"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse();
        var body = response.getContentAsString();

        List<UserDTO> actual = objectMapper.readValue(body, new TypeReference<>() { });

        var expected = taskStatusRepository.findAll().stream().map(taskStatusMapper::map).toList();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/task_statuses/" + testTaskStatus.getId());
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(
                        v -> v.node("slug").isEqualTo(testTaskStatus.getSlug()),
                        v -> v.node("name").isEqualTo(testTaskStatus.getName()));
    }

    @Test
    public void testUpdate() throws  Exception {
        HashMap<String, String> body = new HashMap<>();
        body.put("name", "newStatus");

        var request =
                put("/api/task_statuses/" + testTaskStatus.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body));
        mockMvc.perform(request).andExpect(status().isOk());
        var taskStatus = taskStatusRepository.findById(testTaskStatus.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        Assertions.assertThat(taskStatus.getName())
                .isEqualTo("newStatus");
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/" + testTaskStatus.getId())).andExpect(status().isNoContent());
        var taskStatus = taskStatusRepository.findById(testTaskStatus.getId()).orElse(null);
        assertNull(taskStatus);
    }
}
