package com.hen.flastcard.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hen.flastcard.dto.request.RoleRequest;
import com.hen.flastcard.dto.response.RoleResponse;
import com.hen.flastcard.service.RoleService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;

    private RoleRequest roleRequest;
    private RoleResponse roleResponse;
    private List<RoleResponse> roleResponses;

    @BeforeEach
    void initData() {
        roleRequest = RoleRequest.builder()
                .name("ADMIN")
                .description("Administrator")
                .permissions(Set.of("USER_GET_ALL", "USER_DELETE"))
                .build();

        roleResponse = RoleResponse.builder()
                .name("ADMIN")
                .description("Administrator")
                .build();

        roleResponses = List.of(
                roleResponse,
                RoleResponse.builder().name("USER").description("Normal user").build());
    }

    @Test
    void create_validRequest_success() throws Exception {
        when(roleService.create(roleRequest)).thenReturn(roleResponse);

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.name").value("ADMIN"))
                .andExpect(jsonPath("$.result.description").value("Administrator"));

        verify(roleService).create(roleRequest);
    }

    @Test
    void getAll_success() throws Exception {
        when(roleService.getAll()).thenReturn(roleResponses);

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].name").value("ADMIN"))
                .andExpect(jsonPath("$.result[1].name").value("USER"));

        verify(roleService).getAll();
    }

    @Test
    void delete_success() throws Exception {
        doNothing().when(roleService).delete("ADMIN");

        mockMvc.perform(delete("/api/roles/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000));

        verify(roleService).delete("ADMIN");
    }

    @Test
    void update_validRequest_success() throws Exception {
        when(roleService.update("ADMIN", roleRequest)).thenReturn(roleResponse);

        mockMvc.perform(put("/api/roles/ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.name").value("ADMIN"))
                .andExpect(jsonPath("$.result.description").value("Administrator"));

        verify(roleService).update("ADMIN", roleRequest);
    }
}
