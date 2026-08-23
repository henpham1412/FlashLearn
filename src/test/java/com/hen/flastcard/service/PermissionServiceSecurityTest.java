package com.hen.flastcard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.hen.flastcard.dto.request.PermissionRequest;

@SpringBootTest
@TestPropertySource("/test.properties")
class PermissionServiceSecurityTest {

    @Autowired
    private PermissionService permissionService;

    @Test
    @WithMockUser(authorities = "PERMISSION_GET_ALL")
    void create_withoutRequiredAuthority_fail() {
        var request = PermissionRequest.builder()
                .name("ROLE_CREATE")
                .description("Create role")
                .build();

        assertThrows(AuthorizationDeniedException.class, () -> permissionService.create(request));
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void getAll_withoutRequiredAuthority_fail() {
        assertThrows(AuthorizationDeniedException.class, () -> permissionService.getALl());
    }

    @Test
    @WithMockUser(authorities = "PERMISSION_CREATE")
    void delete_withoutRequiredAuthority_fail() {
        assertThrows(AuthorizationDeniedException.class, () -> permissionService.delete("ROLE_CREATE"));
    }
}
