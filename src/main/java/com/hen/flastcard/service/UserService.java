package com.hen.flastcard.service;

import com.hen.flastcard.dto.request.UserCreationRequest;
import com.hen.flastcard.dto.request.UserUpdationRequest;
import com.hen.flastcard.dto.response.UserResponse;
import com.hen.flastcard.entity.User;
import com.hen.flastcard.enums.Role;
import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.exception.ErrorCode;
import com.hen.flastcard.mapper.UserMapper;
import com.hen.flastcard.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    CurrentUserService currentUserService;
    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        } else if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserById(Long id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(
                        () -> new AppException(ErrorCode.USER_NOT_EXISTED)
                )
        );
    }

    public List<UserResponse> getAll() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return  userRepository
                .findAll().stream().map(userMapper::toUserResponse)
                .toList();
    }
    @Transactional
    public UserResponse updateUser(Long id, UserUpdationRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateUser(user, request);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
    @Transactional
    public String deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        userRepository.deleteById(id);
        return "User has been deleted";
    }

    public UserResponse getMyInfo() {
        return userMapper.toUserResponse(currentUserService.getCurrentUser());
    }
    @Transactional
    public UserResponse updateUser(UserUpdationRequest request) {
        User user = currentUserService.getCurrentUser();
        userMapper.updateUser(user, request);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
}
