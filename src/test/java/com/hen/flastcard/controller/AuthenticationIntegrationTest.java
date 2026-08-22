package com.hen.flastcard.controller;

import com.hen.flastcard.constant.PredefinedRole;
import com.hen.flastcard.dto.request.AuthenticationRequest;
import com.hen.flastcard.entity.Role;
import com.hen.flastcard.entity.User;
import com.hen.flastcard.exception.ErrorCode;
import com.hen.flastcard.repository.RoleRepository;
import com.hen.flastcard.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;


import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@Testcontainers
class AuthenticationIntegrationTest {
    @Container
    static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:8");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driverClassName", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthenticationRequest authenticationRequest;

    @BeforeEach
    void initData() {
        userRepository.deleteAll();

        Role userRole = roleRepository.findById(PredefinedRole.USER_ROLE)
                .orElseThrow();

        User user = User.builder()
                .username("john")
                .email("john@gmail.com")
                .password(passwordEncoder.encode("12345678"))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        authenticationRequest = AuthenticationRequest.builder()
                .email("john@gmail.com")
                .password("12345678")
                .build();
    }

    @Test
    void login_valid_success() throws Exception {
        String content = objectMapper.writeValueAsString(authenticationRequest);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.result.authenticated").value(true))
                .andExpect(header().string(
                        HttpHeaders.SET_COOKIE,
                        Matchers.containsString("refresh_token=")
                ));
    }

    @Test
    void login_userNotFound_fail() throws Exception {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("notfound@gmail.com")
                .password("12345678")
                .build();

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.USER_NOT_EXISTED.getCode()))
                .andExpect(jsonPath("$.message")
                        .value(ErrorCode.USER_NOT_EXISTED.getMessage()));
    }

    @Test
    void login_wrongPassword_fail() throws Exception {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("john@gmail.com")
                .password("wrong-password")
                .build();

        String content = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHENTICATED.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHENTICATED.getMessage()));
    }

    @Test
    void login_emptyEmail_fail() throws Exception {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("")
                .password("12345678")
                .build();

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());
    }
}
