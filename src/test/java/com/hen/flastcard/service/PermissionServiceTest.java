package com.hen.flastcard.service;

import static org.mockito.Mockito.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hen.flastcard.dto.request.PermissionRequest;
import com.hen.flastcard.dto.response.PermissionResponse;
import com.hen.flastcard.entity.Permission;
import com.hen.flastcard.mapper.PermissionMapper;
import com.hen.flastcard.repository.PermissionRepository;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @InjectMocks
    private PermissionService permissionService;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PermissionMapper permissionMapper;

    private PermissionRequest request;
    private PermissionResponse permissionResponse;
    private PermissionResponse permissionResponse2;
    private Permission permission;
    private Permission permission2;

    @BeforeEach
    void initData() {
        request = PermissionRequest.builder()
                .name("ROLE_CREATE")
                .description("Create role")
                .build();

        permission = Permission.builder()
                .name("ROLE_CREATE")
                .description("Create role")
                .build();

        permission2 = Permission.builder()
                .name("ROLE_DELETE")
                .description("Delete role")
                .build();

        permissionResponse = PermissionResponse.builder()
                .name("ROLE_CREATE")
                .description("Create role")
                .build();

        permissionResponse2 = PermissionResponse.builder()
                .name("ROLE_DELETE")
                .description("Delete role")
                .build();
    }

    @Test
    void create_valid_success() {
        when(permissionMapper.toPermission(request)).thenReturn(permission);

        when(permissionRepository.save(permission)).thenReturn(permission);

        when(permissionMapper.toPermissionResponse(permission)).thenReturn(permissionResponse);

        var response = permissionService.create(request);

        Assertions.assertThat(response).isEqualTo(permissionResponse);

        verify(permissionMapper).toPermission(request);

        verify(permissionRepository).save(permission);

        verify(permissionMapper).toPermissionResponse(permission);
    }

    @Test
    void getAll_valid_success() {
        when(permissionRepository.findAll()).thenReturn(List.of(permission, permission2));

        when(permissionMapper.toPermissionResponse(permission)).thenReturn(permissionResponse);

        when(permissionMapper.toPermissionResponse(permission2)).thenReturn(permissionResponse2);

        var response = permissionService.getALl();

        Assertions.assertThat(response).containsExactly(permissionResponse, permissionResponse2);

        verify(permissionRepository).findAll();

        verify(permissionMapper).toPermissionResponse(permission);

        verify(permissionMapper).toPermissionResponse(permission2);
    }

    @Test
    void getAll_empty_success() {
        when(permissionRepository.findAll()).thenReturn(List.of());

        var response = permissionService.getALl();

        Assertions.assertThat(response).isEmpty();

        verify(permissionRepository).findAll();

        verify(permissionMapper, never()).toPermissionResponse(any());
    }

    @Test
    void delete_valid_success() {
        permissionService.delete("ROLE_CREATE");

        verify(permissionRepository).deleteById("ROLE_CREATE");
    }
}
