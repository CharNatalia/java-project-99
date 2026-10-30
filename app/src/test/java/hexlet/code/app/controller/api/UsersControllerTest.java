package hexlet.code.app.controller.api;

import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@ActiveProfiles("test")
public class UsersControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private User testUser;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        testUser = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .set(Select.field(User::getEmail), faker.internet().emailAddress())
                .create();
        userRepository.save(testUser);
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .set(Select.field(User::getEmail), faker.internet().emailAddress())
                .create();
        var request =
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isCreated());

        var user = userRepository.findByEmail(data.getEmail()).orElse(null);

        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(user.getLastName()).isEqualTo(data.getLastName());
    }

    @Test
    public void testIndex() throws Exception {
        var response =
                mockMvc.perform(get("/api/users"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse();
        var body = response.getContentAsString();

        List<UserDTO> actual = objectMapper.readValue(body, new TypeReference<>() { });

        var expected = userRepository.findAll().stream().map(userMapper::map).toList();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/users/" + testUser.getId());
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body)
                .and(
                        v -> v.node("email").isEqualTo(testUser.getEmail()),
                        v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                        v -> v.node("lastName").isEqualTo(testUser.getLastName()));
    }

    @Test
    public void testUpdate() throws  Exception {
        HashMap<String, String> body = new HashMap<>();
        body.put("email", "jack@yahoo.com");
        body.put("password", "new-password");

        var request =
                put("/api/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body));
        mockMvc.perform(request).andExpect(status().isOk());
        var user = userRepository.findById(testUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        Assertions.assertThat(user.getEmail())
                .isEqualTo("jack@yahoo.com");

        Assertions.assertThat(
                        passwordEncoder.matches("new-password", user.getPassword()))
                .isTrue();
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId())).andExpect(status().isNoContent());
        var user = userRepository.findById(testUser.getId()).orElse(null);
        assertNull(user);
    }

}
