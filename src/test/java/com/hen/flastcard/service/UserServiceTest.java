package com.hen.flastcard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hen.flastcard.constant.PredefinedRole;
import com.hen.flastcard.dto.request.UserCreationRequest;
import com.hen.flastcard.dto.request.UserUpdationRequest;
import com.hen.flastcard.dto.response.UserResponse;
import com.hen.flastcard.entity.Role;
import com.hen.flastcard.entity.User;
import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.exception.ErrorCode;
import com.hen.flastcard.mapper.UserMapper;
import com.hen.flastcard.repository.RoleRepository;
import com.hen.flastcard.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserCreationRequest request;
    private UserUpdationRequest userUpdationRequest;
    private UserResponse userResponse;
    private UserResponse response2;
    private User user;
    private User user2;
    private List<Role> userRoles = new ArrayList<>();
    private Role userRole;

    @BeforeEach
    void initData() {

        request = UserCreationRequest.builder()
                .username("john")
                .email("john@gmail.com")
                .password("12345678")
                .build();
        userUpdationRequest = UserUpdationRequest.builder().username("john").build();

        userResponse = UserResponse.builder()
                .id(1L)
                .username("john")
                .email("john@gmail.com")
                .build();

        user = User.builder().id(1L).username("john").email("john@gmail.com")
                .password("12345678")
                .build();
        user2 = User.builder().id(2L).username("alice").email("alice@gmail.com").build();
        userRole = new Role().builder().name("USER").description("User's role").build();
        userRoles.add(userRole);
        response2 = UserResponse.builder()
                .id(2L)
                .username("alice")
                .email("alice@gmail.com")
                .build();
    }

    @Test
    void getMyInfo_validRequest_success() {
        // GIVEN
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        // WHEN
        var response = userService.getMyInfo();
        Assertions.assertThat(response)
                .isEqualTo(userResponse);
        verify(currentUserService).getCurrentUser();
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void getUserById_valid_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        var response = userService.getUserById(1L);
        Assertions.assertThat(response).isEqualTo(userResponse);
        verify(userRepository).findById(1L);
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void getUserById_invalid_fail() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        var exception = assertThrows(AppException.class, () -> userService.getUserById(1L));
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);
        verify(userRepository).findById(1L);
        verify(userMapper, never()).toUserResponse(any());
    }

    @Test
    void updateUser_valid_success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        var response = userService.updateUser(userUpdationRequest);

        Assertions.assertThat(response)
                .isEqualTo(userResponse);
        verify(currentUserService).getCurrentUser();
        verify(userMapper).updateUser(user, userUpdationRequest);
        verify(userRepository).save(user);
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void deleteUser_valid_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        var response = userService.deleteUser(1L);
        Assertions.assertThat(response).isEqualTo("User has been deleted");
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_invalid_fail() {
        when(userRepository.existsById(1L)).thenReturn(false);
        var exception = assertThrows(AppException.class, () -> userService.deleteUser(1L));
        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_EXISTED);
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getAllUser_valid_success() {
        when(userRepository.findAll()).thenReturn(List.of(user, user2));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        when(userMapper.toUserResponse(user2)).thenReturn(response2);

        var response = userService.getAll();

        Assertions.assertThat(response)
                .containsExactly(userResponse, response2);
        verify(userRepository).findAll();
        verify(userMapper).toUserResponse(user);
        verify(userMapper).toUserResponse(user2);
    }

    @Test
    void getAll_empty_success() {
        when(userRepository.findAll()).thenReturn(List.of());
        var response = userService.getAll();
        Assertions.assertThat(response).isEmpty();
        verify(userRepository).findAll();
        verify(userMapper, never()).toUserResponse(any());
    }

    @Test
    void updateUser_nonJWT_valid_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findAllById(userUpdationRequest.getRoles())).thenReturn( userRoles);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        var response = userService.updateUser(1L, userUpdationRequest);
        Assertions.assertThat(response).isEqualTo(userResponse);
        Assertions.assertThat(user.getRoles())
                .containsExactly(userRole);
        verify(userRepository).findById(1L);
        verify(userMapper).updateUser(user, userUpdationRequest);
        verify(roleRepository).findAllById(userUpdationRequest.getRoles());
        verify(userRepository).save(user);
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void updateUser_nonJWT_invalid_fail() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        var exception = assertThrows(AppException.class, () -> userService.updateUser(1L, userUpdationRequest));
        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_EXISTED);
        verify(userRepository).findById(1L);
        verify(userMapper, never()).updateUser(any(), any());
        verify(roleRepository, never()).findAllById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_valid_success() {
        when(userMapper.toUser(request)).thenReturn(user);
        when(roleRepository.findById(PredefinedRole.USER_ROLE)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("12345678"))
                .thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        var response = userService.createUser(request);

        Assertions.assertThat(response.getId()).isEqualTo(1L);
        Assertions.assertThat(response.getUsername()).isEqualTo("john");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        Assertions.assertThat(savedUser.getPassword())
                .isEqualTo("encoded-password");
        Assertions.assertThat(savedUser.getRoles())
                .containsExactly(userRole);

        verify(userMapper).toUserResponse(user);
        verify(roleRepository).findById(PredefinedRole.USER_ROLE);
        verify(passwordEncoder).encode("12345678");
        verify(userMapper).toUser(request);
    }

    @Test
    void createUser_roleNotFound_fail() {
        when(userMapper.toUser(request)).thenReturn(user);
        when(passwordEncoder.encode("12345678"))
                .thenReturn("encoded-password");
        when(roleRepository.findById(PredefinedRole.USER_ROLE))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                AppException.class,
                () -> userService.createUser(request)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.ROLE_NOT_EXISTED);

        verify(userMapper).toUser(request);
        verify(passwordEncoder).encode("12345678");
        verify(roleRepository).findById(PredefinedRole.USER_ROLE);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toUserResponse(any());
    }

    @Test
    void createUser_userExist_fail() {
        when(userMapper.toUser(request)).thenReturn(user);
        when(passwordEncoder.encode("12345678"))
                .thenReturn("encoded-password");
        when(roleRepository.findById(PredefinedRole.USER_ROLE)).thenReturn(Optional.of(userRole));
        when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException("User already exists"));
        var exception = assertThrows(AppException.class, () -> userService.createUser(request));

        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_EXISTED);

        verify(userMapper).toUser(request);
        verify(passwordEncoder).encode("12345678");
        verify(roleRepository).findById(PredefinedRole.USER_ROLE);
        verify(userRepository).save(user);
        verify(userMapper, never()).toUserResponse(any());
    }
}
