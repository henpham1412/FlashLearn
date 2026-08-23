package com.hen.flastcard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.hen.flastcard.dto.request.UserUpdationRequest;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceSecurityTest {
    @Autowired
    private UserService userService;

    @Test
    @WithMockUser(roles = "USER")
    void deleteUser_notAdmin_fail() {
        assertThrows(AuthorizationDeniedException.class, () -> userService.deleteUser(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAll_notAdmin_fail() {
        assertThrows(AuthorizationDeniedException.class, () -> userService.getAll());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUser_nonJWT_notAdmin_fail() {
        var request = UserUpdationRequest.builder().username("john").build();
        assertThrows(AuthorizationDeniedException.class, () -> userService.updateUser(1L, request));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserById_notAdmin_fail() {
        assertThrows(AuthorizationDeniedException.class, () -> userService.getUserById(1L));
    }
}
