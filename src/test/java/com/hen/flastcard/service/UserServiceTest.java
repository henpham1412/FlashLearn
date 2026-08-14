package com.hen.flastcard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.hen.flastcard.dto.request.UserCreationRequest;
import com.hen.flastcard.dto.response.UserResponse;
import com.hen.flastcard.entity.User;
import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.repository.UserRepository;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CurrentUserService currentUserService;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private User user;

    @BeforeEach
    void initData() {

        request = UserCreationRequest.builder()
                .username("john")
                .email("john@gmail.com")
                .password("12345678")
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .username("john")
                .email("john@gmail.com")
                .build();

        user = User.builder().id(1L).username("john").email("john@gmail.com").build();
    }

    //    @Test
    //    void createUser_valid_success() {
    //        // GIVEN
    //        when(userRepository.existsByUsername(anyString())).thenReturn(false);
    //        when(userRepository.existsByEmail(anyString())).thenReturn(false);
    //        when(userRepository.save(any())).thenReturn(user);
    //
    //        // WHEN
    //        var response = userService.createUser(request);
    //
    //        // THEN
    //        Assertions.assertThat(response.getId()).isEqualTo(1);
    //        Assertions.assertThat(response.getUsername()).isEqualTo("john");
    //    }

    @Test
    void createUser_userExist_fail() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        var exception = assertThrows(AppException.class, () -> userService.createUser(request));
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);
    }

    @Test
    void getMyInfo_validRequest_success() {
        // GIVEN
        when(currentUserService.getCurrentUser()).thenReturn(user);

        // WHEN
        var response = userService.getMyInfo();

        Assertions.assertThat(response.getUsername()).isEqualTo("john");
        Assertions.assertThat(response.getId()).isEqualTo(1);
    }
}
