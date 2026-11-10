package hexlet.code.app.controller.api;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.ModelGenerator;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private TaskStatus testTaskStatus;
    private Task testTask;
    private User testUser;
    private Label testLabel;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private Faker faker;

    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = modelGenerator.createUser();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testTaskStatus = modelGenerator.createTaskStatus();
        taskStatusRepository.save(testTaskStatus);

        testLabel = modelGenerator.createLabel();
        labelRepository.save(testLabel);


        testTask = modelGenerator.createTask();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.addLabel(testLabel);
        taskRepository.save(testTask);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/tasks/" + testTask.getId()).with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(
                        v -> v.node("title").isEqualTo(testTask.getName()),
                        v -> v.node("status").isEqualTo(testTaskStatus.getSlug()));
    }

    @Test
    @Transactional
    public void testIndexWithParams() throws Exception {
        for (var i = 0; i < 5; i++) {
            var task = modelGenerator.createTask();
            task.setTaskStatus(testTaskStatus);
            taskRepository.save(task);
        }

        var body = mockMvc.perform(get("/api/tasks")
                        .param("titleCont", testTask.getName())
                        .param("assigneeId", testTask.getAssignee().getId().toString())
                        .param("status", testTask.getTaskStatus().getSlug())
                        .param("labelId", testLabel.getId().toString())
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<TaskDTO> actual = objectMapper.readValue(body, new TypeReference<>() {
        });
        Assertions.assertThat(actual).containsExactly(taskMapper.map(testTask));
    }

    @Test
    @Transactional
    public void testIndex() throws Exception {
        var body = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<TaskDTO> actual = objectMapper.readValue(body, new TypeReference<>() {
        });
        var expected = taskRepository.findAll().stream().map(taskMapper::map).toList();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(TaskCreateDTO.class)
                .ignore(Select.field(TaskCreateDTO::getAssigneeId))
                .ignore(Select.field(TaskCreateDTO::getTaskLabelIds))
                .set(Select.field(TaskCreateDTO::getTitle), faker.name().title())
                .set(Select.field(TaskCreateDTO::getStatus), testTaskStatus.getSlug())
                .create();
        var request = post("/api/tasks").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        var id = objectMapper.readTree(response.getContentAsString())
                .get("id")
                .asLong();

        var task = taskRepository.findById(id)
                .orElse(null);
        assertNotNull(task);
        assertThat(task.getName()).isEqualTo(data.getTitle());
    }

    @Test
    public void testCreateExceptions() throws Exception {
        var data = Instancio.of(TaskCreateDTO.class)
                .ignore(Select.field(TaskCreateDTO::getAssigneeId))
                .ignore(Select.field(TaskCreateDTO::getTaskLabelIds))
                .ignore(Select.field(TaskCreateDTO::getStatus))
                .set(Select.field(TaskCreateDTO::getTitle), faker.name().title())
                .create();


        var request = post("/api/tasks").with(token)
                .contentType(MediaType.APPLICATION_JSON);

        data.setStatus("Wrong status");
        assertNotFound(request, data, "Status not found");

        data.setStatus(testTaskStatus.getSlug());
        data.setAssigneeId(9999L);
        assertNotFound(request, data, "User not found");

        data.setAssigneeId(null);
        data.setTaskLabelIds(List.of(99999L));
        assertNotFound(request, data, "Label not found");
    }

    @Test
    public void testUpdateExceptions() throws Exception {
        var request = put("/api/tasks/" + testTask.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON);

        HashMap<String, Object> data = new HashMap<>();
        data.put("status", "Wrong status");
        assertNotFound(request, data, "Status not found");

        data.clear();
        data.put("assignee_id", 9999L);
        assertNotFound(request, data, "User not found");

        data.clear();
        data.put("taskLabelIds", List.of(9999L));
        assertNotFound(request, data, "Label not found");
    }

    private void assertNotFound(MockHttpServletRequestBuilder request, Object data, String message) throws Exception {
        mockMvc.perform(request.content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(message));
    }

    @Test
    public void testUpdate() throws Exception {
        HashMap<String, String> body = new HashMap<>();
        body.put("title", "New title");
        body.put("content", "New content");

        var request = put("/api/tasks/" + testTask.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));
        mockMvc.perform(request).andExpect(status().isOk());
        var task = taskRepository.findById(testTask.getId())
                .orElse(null);
        assertNotNull(task);
        Assertions.assertThat(task.getName()).isEqualTo("New title");
        Assertions.assertThat(task.getDescription()).isEqualTo("New content");
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + testTask.getId()).with(token))
                .andExpect(status().isNoContent());
        var deletedTask = taskRepository.findById(testTask.getId()).orElse(null);
        assertNull(deletedTask);
    }

    @Test
    public void testEqualsAndHashCodeTask() {
        var first = testTask;
        var second = modelGenerator.createTask();
        second.setTaskStatus(testTaskStatus);

        assertThat(first).isEqualTo(first);
        assertThat(first).isNotEqualTo(null);
        assertThat(first).isNotEqualTo("task");
        assertThat(first).isNotEqualTo(second);

        first.setId(1L);
        second.setId(1L);

        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());

        second.setId(2L);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    public void testEqualsAndHashCodeTaskDTO() {
        var first = taskMapper.map(testTask);
        var task = modelGenerator.createTask();
        task.setTaskStatus(testTaskStatus);
        var second = taskMapper.map(task);

        assertThat(first).isEqualTo(first);
        assertThat(first).isNotEqualTo(null);
        assertThat(first).isNotEqualTo("task");
        assertThat(second.hashCode()).isEqualTo(taskMapper.map(task).hashCode());
        assertThat(first).isNotEqualTo(second);
    }
}
