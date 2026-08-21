package com.hen.flastcard.service;

import com.hen.flastcard.dto.request.RoleRequest;
import com.hen.flastcard.dto.response.RoleResponse;
import com.hen.flastcard.entity.Permission;
import com.hen.flastcard.entity.Role;
import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.exception.ErrorCode;
import com.hen.flastcard.mapper.RoleMapper;
import com.hen.flastcard.repository.PermissionRepository;
import com.hen.flastcard.repository.RoleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PermissionRepository permissionRepository;

    private RoleRequest request;
    private RoleResponse roleResponse;
    private RoleResponse roleResponse2;
    private Role role;
    private Role role2;
    private Permission permission;
    private Permission permission2;
    private List<Permission> permissions;

    @BeforeEach
    void initData() {
        permission = Permission.builder()
                .name("ROLE_CREATE")
                .build();

        permission2 = Permission.builder()
                .name("ROLE_UPDATE")
                .build();

        permissions = List.of(permission, permission2);

        request = RoleRequest.builder()
                .name("ADMIN")
                .permissions(Set.of(
                        "ROLE_CREATE",
                        "ROLE_UPDATE"
                ))
                .build();

        role = Role.builder()
                .name("ADMIN")
                .description("Administrator")
                .permissions(new HashSet<>(permissions))
                .build();

        role2 = Role.builder()
                .name("USER")
                .description("Normal user")
                .permissions(Set.of(permission))
                .build();

        roleResponse = RoleResponse.builder()
                .name("ADMIN")
                .description("Administrator")
                .build();

        roleResponse2 = RoleResponse.builder()
                .name("USER")
                .description("Normal user")
                .build();
    }

    @Test
    void create_valid_success() {
        when(roleMapper.toRole(request))
                .thenReturn(role);

        when(permissionRepository.findAllById(request.getPermissions()))
                .thenReturn(permissions);

        when(roleRepository.save(role))
                .thenReturn(role);

        when(roleMapper.toRoleResponse(role))
                .thenReturn(roleResponse);

        var response = roleService.create(request);

        Assertions.assertThat(response)
                .isEqualTo(roleResponse);

        Assertions.assertThat(role.getPermissions())
                .containsExactlyInAnyOrder(permission, permission2);

        verify(roleMapper)
                .toRole(request);

        verify(permissionRepository)
                .findAllById(request.getPermissions());

        verify(roleRepository)
                .save(role);

        verify(roleMapper)
                .toRoleResponse(role);
    }

    @Test
    void getAll_valid_success() {
        when(roleRepository.findAll())
                .thenReturn(List.of(role, role2));

        when(roleMapper.toRoleResponse(role))
                .thenReturn(roleResponse);

        when(roleMapper.toRoleResponse(role2))
                .thenReturn(roleResponse2);

        var response = roleService.getAll();

        Assertions.assertThat(response)
                .containsExactly(roleResponse, roleResponse2);

        verify(roleRepository)
                .findAll();

        verify(roleMapper)
                .toRoleResponse(role);

        verify(roleMapper)
                .toRoleResponse(role2);
    }

    @Test
    void getAll_empty_success() {
        when(roleRepository.findAll())
                .thenReturn(List.of());

        var response = roleService.getAll();

        Assertions.assertThat(response)
                .isEmpty();

        verify(roleRepository)
                .findAll();

        verify(roleMapper, never())
                .toRoleResponse(any());
    }

    @Test
    void delete_valid_success() {
        roleService.delete("ADMIN");

        verify(roleRepository)
                .deleteById("ADMIN");
    }

    @Test
    void update_valid_success() {
        when(roleRepository.findById("ADMIN"))
                .thenReturn(Optional.of(role));

        when(permissionRepository.findAllById(request.getPermissions()))
                .thenReturn(permissions);

        when(roleRepository.save(role))
                .thenReturn(role);

        when(roleMapper.toRoleResponse(role))
                .thenReturn(roleResponse);

        var response = roleService.update("ADMIN", request);

        Assertions.assertThat(response)
                .isEqualTo(roleResponse);

        Assertions.assertThat(role.getPermissions())
                .containsExactlyInAnyOrder(permission, permission2);

        verify(roleRepository)
                .findById("ADMIN");

        verify(permissionRepository)
                .findAllById(request.getPermissions());

        verify(roleMapper)
                .updateRole(role, request);

        verify(roleRepository)
                .save(role);

        verify(roleMapper)
                .toRoleResponse(role);
    }

    @Test
    void update_roleNotFound_fail() {
        when(roleRepository.findById("ADMIN"))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                AppException.class,
                () -> roleService.update("ADMIN", request)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.ROLE_NOT_EXISTED);

        verify(roleRepository)
                .findById("ADMIN");

        verify(permissionRepository, never())
                .findAllById(any());

        verify(roleMapper, never())
                .updateRole(any(), any());

        verify(roleRepository, never())
                .save(any());

        verify(roleMapper, never())
                .toRoleResponse(any());
    }
}