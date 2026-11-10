package hexlet.code.app.controller.api;

import hexlet.code.app.dto.AuthRequest;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserCreateDTO userData;

    private AuthRequest authRequest;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        userData = new UserCreateDTO();
        userData.setEmail("testEmail@test.com");
        userData.setPassword("testPassword");
        userService.createUser(userData);

        authRequest = new AuthRequest();
    }

    @Test
    public void testWrongUsername() throws Exception {
        var body = new HashMap<>();
        body.put("username", "wrong email");
        body.put("password", "password");

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void testWrongPassword() throws Exception {
        authRequest.setUsername(userData.getEmail());
        authRequest.setPassword("wrong password");


        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest));

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthorize() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());

        var body = new HashMap<>();
        body.put("username", userData.getEmail());
        body.put("password", "testPassword");

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));

        var token = mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        mockMvc.perform(get("/api/users").
                header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
