package com.hen.flastcard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.hen.flastcard.dto.request.RoleRequest;

@SpringBootTest
@TestPropertySource("/test.properties")
class RoleServiceSecurityTest {

    @Autowired
    private RoleService roleService;

    @Test
    @WithMockUser(authorities = "ROLE_GET_ALL")
    void create_withoutRequiredAuthority_fail() {
        var request = RoleRequest.builder().name("ADMIN").permissions(Set.of()).build();

        assertThrows(AuthorizationDeniedException.class, () -> roleService.create(request));
    }

    @Test
    @WithMockUser(authorities = "ROLE_CREATE")
    void getAll_withoutRequiredAuthority_fail() {
        assertThrows(AuthorizationDeniedException.class, () -> roleService.getAll());
    }

    @Test
    @WithMockUser(authorities = "ROLE_CREATE")
    void delete_withoutRequiredAuthority_fail() {
        assertThrows(AuthorizationDeniedException.class, () -> roleService.delete("ADMIN"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_CREATE")
    void update_withoutRequiredAuthority_fail() {
        var request = RoleRequest.builder().name("ADMIN").permissions(Set.of()).build();

        assertThrows(AuthorizationDeniedException.class, () -> roleService.update("ADMIN", request));
    }
}
