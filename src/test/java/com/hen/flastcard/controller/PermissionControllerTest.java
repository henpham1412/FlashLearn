package com.hen.flastcard.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.hen.flastcard.dto.request.PermissionRequest;
import com.hen.flastcard.dto.response.PermissionResponse;
import com.hen.flastcard.service.PermissionService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PermissionController.class)
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PermissionService permissionService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;

    private PermissionRequest permissionRequest;
    private PermissionResponse permissionResponse;
    private List<PermissionResponse> permissionResponses;

    @BeforeEach
    void initData() {
        permissionRequest = PermissionRequest.builder()
                .name("USER_GET_ALL")
                .description("Get all users")
                .build();

        permissionResponse = PermissionResponse.builder()
                .name("USER_GET_ALL")
                .description("Get all users")
                .build();

        permissionResponses = List.of(
                permissionResponse,
                PermissionResponse.builder()
                        .name("USER_DELETE")
                        .description("Delete user")
                        .build());
    }

    @Test
    void create_validRequest_success() throws Exception {
        when(permissionService.create(permissionRequest)).thenReturn(permissionResponse);

        mockMvc.perform(post("/api/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.name").value("USER_GET_ALL"))
                .andExpect(jsonPath("$.result.description").value("Get all users"));

        verify(permissionService).create(permissionRequest);
    }

    @Test
    void getAll_success() throws Exception {
        when(permissionService.getALl()).thenReturn(permissionResponses);

        mockMvc.perform(get("/api/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].name").value("USER_GET_ALL"))
                .andExpect(jsonPath("$.result[1].name").value("USER_DELETE"));

        verify(permissionService).getALl();
    }

    @Test
    void delete_success() throws Exception {
        doNothing().when(permissionService).delete("USER_DELETE");

        mockMvc.perform(delete("/api/permissions/USER_DELETE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000));

        verify(permissionService).delete("USER_DELETE");
    }
}
