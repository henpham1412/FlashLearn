package com.hen.flastcard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.hen.flastcard.exception.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.hen.flastcard.entity.User;
import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CurrentUserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    private CurrentUserService currentUserService;

    private User user;

    @BeforeEach
    void initData() {
        user = User.builder().id(1L).username("john").email("john@gmail.com").build();
    }

    @Test
    @WithMockUser("john")
    void getCurrentUser_valid_success() {
        // GIVEN
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        //  WHEN
        var response = currentUserService.getCurrentUser();

        //  THEN
        Assertions.assertThat(response.getUsername()).isEqualTo("john");
        Assertions.assertThat(response.getId()).isEqualTo(1);
    }

    @Test
    @WithMockUser("john")
    void getCurrentUser_userNotFound_error() {
        // GIVEN
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //  WHEN, THEN
        var exception = assertThrows(AppException.class, () -> currentUserService.getCurrentUser());
        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_EXISTED);
    }
}
