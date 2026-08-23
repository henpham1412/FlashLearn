package com.hen.flastcard.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hen.flastcard.configuration.CustomJwtDecoder;
import com.hen.flastcard.dto.request.UserCreationRequest;
import com.hen.flastcard.dto.request.UserUpdationRequest;
import com.hen.flastcard.dto.response.UserResponse;
import com.hen.flastcard.service.UserService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;

    private UserCreationRequest creationRequest;
    private UserUpdationRequest updationRequest;
    private UserResponse userResponse;
    private List<UserResponse> userResponses;

    @BeforeEach
    void initData() {
        creationRequest = UserCreationRequest.builder()
                .username("john")
                .email("john@gmail.com")
                .password("12345678")
                .build();

        updationRequest = UserUpdationRequest.builder().username("john").build();

        userResponse = UserResponse.builder()
                .id(1L)
                .username("john")
                .email("john@gmail.com")
                .build();

        userResponses = List.of(
                userResponse,
                UserResponse.builder()
                        .id(2L)
                        .username("alice")
                        .email("alice@gmail.com")
                        .build());
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        when(userService.createUser(creationRequest)).thenReturn(userResponse);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.username").value("john"))
                .andExpect(jsonPath("$.result.email").value("john@gmail.com"));

        verify(userService).createUser(creationRequest);
    }

    @Test
    void getUserById_validRequest_success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.username").value("john"))
                .andExpect(jsonPath("$.result.email").value("john@gmail.com"));

        verify(userService).getUserById(1L);
    }

    @Test
    void getAll_success() throws Exception {
        when(userService.getAll()).thenReturn(userResponses);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].id").value(1))
                .andExpect(jsonPath("$.result[0].username").value("john"))
                .andExpect(jsonPath("$.result[1].id").value(2))
                .andExpect(jsonPath("$.result[1].username").value("alice"));

        verify(userService).getAll();
    }

    @Test
    void updateUser_validRequest_success() throws Exception {
        when(userService.updateUser(1L, updationRequest)).thenReturn(userResponse);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.username").value("john"));

        verify(userService).updateUser(1L, updationRequest);
    }

    @Test
    void deleteUser_validRequest_success() throws Exception {
        when(userService.deleteUser(1L)).thenReturn("User has been deleted");

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result").value("User has been deleted"));

        verify(userService).deleteUser(1L);
    }

    @Test
    void getMyInfo_success() throws Exception {
        when(userService.getMyInfo()).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.username").value("john"))
                .andExpect(jsonPath("$.result.email").value("john@gmail.com"));

        verify(userService).getMyInfo();
    }

    @Test
    void updateMyInfo_validRequest_success() throws Exception {
        when(userService.updateUser(updationRequest)).thenReturn(userResponse);

        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.username").value("john"));

        verify(userService).updateUser(updationRequest);
    }
}
