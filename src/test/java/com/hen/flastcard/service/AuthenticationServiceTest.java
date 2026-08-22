package com.hen.flastcard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hen.flastcard.dto.request.AuthenticationRequest;
import com.hen.flastcard.dto.request.IntrospectRequest;
import com.hen.flastcard.entity.InvalidatedToken;
import com.hen.flastcard.entity.User;
import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.exception.ErrorCode;
import com.hen.flastcard.repository.InvalidatedTokenRepository;
import com.hen.flastcard.repository.UserRepository;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    private AuthenticationRequest authenticationRequest;
    private User user;

    private final String accessToken = "access-token";
    private final String refreshToken = "refresh-token";
    private final String newAccessToken = "new-access-token";
    private final String newRefreshToken = "new-refresh-token";

    @BeforeEach
    void initData() {
        authenticationRequest = AuthenticationRequest.builder()
                .email("john@gmail.com")
                .password("12345678")
                .build();

        user = User.builder()
                .id(1L)
                .username("john")
                .email("john@gmail.com")
                .password("encoded-password")
                .build();
    }

    @Test
    void authenticate_valid_success() {
        when(userRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword()))
                .thenReturn(true);

        when(jwtService.generateAccessToken(user)).thenReturn(accessToken);

        when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);

        var response = authenticationService.authenticate(authenticationRequest);

        Assertions.assertThat(response.isAuthenticated()).isTrue();

        Assertions.assertThat(response.getAccessToken()).isEqualTo(accessToken);

        Assertions.assertThat(response.getRefreshToken()).isEqualTo(refreshToken);

        verify(userRepository).findByEmail(authenticationRequest.getEmail());

        verify(passwordEncoder).matches(authenticationRequest.getPassword(), user.getPassword());

        verify(jwtService).generateAccessToken(user);

        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    void authenticate_userNotFound_fail() {
        when(userRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.empty());

        var exception =
                assertThrows(AppException.class, () -> authenticationService.authenticate(authenticationRequest));

        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);

        verify(userRepository).findByEmail(authenticationRequest.getEmail());

        verify(passwordEncoder, never()).matches(anyString(), anyString());

        verify(jwtService, never()).generateAccessToken(any());

        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    void authenticate_wrongPassword_fail() {
        when(userRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword()))
                .thenReturn(false);

        var exception =
                assertThrows(AppException.class, () -> authenticationService.authenticate(authenticationRequest));

        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHENTICATED);

        verify(userRepository).findByEmail(authenticationRequest.getEmail());

        verify(passwordEncoder).matches(authenticationRequest.getPassword(), user.getPassword());

        verify(jwtService, never()).generateAccessToken(any());

        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    void introspect_validToken_success() throws Exception {
        IntrospectRequest request =
                IntrospectRequest.builder().token(accessToken).build();

        doNothing().when(jwtService).verifyAccessToken(accessToken);

        var response = authenticationService.introspect(request);

        Assertions.assertThat(response.isValid()).isTrue();

        verify(jwtService).verifyAccessToken(accessToken);
    }

    @Test
    void introspect_invalidToken_fail() throws Exception {
        IntrospectRequest request =
                IntrospectRequest.builder().token(accessToken).build();

        doThrow(new AppException(ErrorCode.UNAUTHENTICATED)).when(jwtService).verifyAccessToken(accessToken);

        var response = authenticationService.introspect(request);

        Assertions.assertThat(response.isValid()).isFalse();

        verify(jwtService).verifyAccessToken(accessToken);
    }

    @Test
    void logout_tokenNotInvalidated_success() throws Exception {
        String jit = "jit-123";
        Date expiryTime = new Date(System.currentTimeMillis() + 60_000);

        SignedJWT signedJWT = createSignedJWT(jit, user.getEmail(), expiryTime);

        when(jwtService.verifyRefreshToken(refreshToken)).thenReturn(signedJWT);

        when(invalidatedTokenRepository.existsById(jit)).thenReturn(false);

        authenticationService.logout(refreshToken);

        ArgumentCaptor<InvalidatedToken> tokenCaptor = ArgumentCaptor.forClass(InvalidatedToken.class);

        verify(invalidatedTokenRepository).existsById(jit);

        verify(invalidatedTokenRepository).save(tokenCaptor.capture());

        InvalidatedToken invalidatedToken = tokenCaptor.getValue();

        Assertions.assertThat(invalidatedToken.getId()).isEqualTo(jit);

        Assertions.assertThat(invalidatedToken.getExpiryTime()).isEqualTo(expiryTime);
    }

    @Test
    void logout_tokenAlreadyInvalidated_success() throws Exception {
        String jit = "jit-123";
        Date expiryTime = new Date(System.currentTimeMillis() + 60_000);

        SignedJWT signedJWT = createSignedJWT(jit, user.getEmail(), expiryTime);

        when(jwtService.verifyRefreshToken(refreshToken)).thenReturn(signedJWT);

        when(invalidatedTokenRepository.existsById(jit)).thenReturn(true);

        authenticationService.logout(refreshToken);

        verify(invalidatedTokenRepository).existsById(jit);

        verify(invalidatedTokenRepository, never()).save(any());
    }

    @Test
    void refresh_valid_success() throws Exception {
        String jit = "jit-123";
        Date expiryTime = new Date(System.currentTimeMillis() + 60_000);

        SignedJWT signedJWT = createSignedJWT(jit, user.getEmail(), expiryTime);

        when(jwtService.verifyRefreshToken(refreshToken)).thenReturn(signedJWT);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(jwtService.generateAccessToken(user)).thenReturn(newAccessToken);

        when(jwtService.generateRefreshToken(user)).thenReturn(newRefreshToken);

        var response = authenticationService.refresh(refreshToken);

        Assertions.assertThat(response.getAccessToken()).isEqualTo(newAccessToken);

        Assertions.assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);

        verify(jwtService).verifyRefreshToken(refreshToken);

        ArgumentCaptor<InvalidatedToken> tokenCaptor = ArgumentCaptor.forClass(InvalidatedToken.class);

        verify(invalidatedTokenRepository).save(tokenCaptor.capture());

        InvalidatedToken invalidatedToken = tokenCaptor.getValue();

        Assertions.assertThat(invalidatedToken.getId()).isEqualTo(jit);

        Assertions.assertThat(invalidatedToken.getExpiryTime()).isEqualTo(expiryTime);

        verify(userRepository).findByEmail(user.getEmail());

        verify(jwtService).generateAccessToken(user);

        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    void refresh_userNotFound_fail() throws Exception {
        String jit = "jit-123";
        Date expiryTime = new Date(System.currentTimeMillis() + 60_000);

        SignedJWT signedJWT = createSignedJWT(jit, user.getEmail(), expiryTime);

        when(jwtService.verifyRefreshToken(refreshToken)).thenReturn(signedJWT);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> authenticationService.refresh(refreshToken));

        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHENTICATED);

        verify(jwtService).verifyRefreshToken(refreshToken);

        verify(invalidatedTokenRepository).save(any(InvalidatedToken.class));

        verify(userRepository).findByEmail(user.getEmail());

        verify(jwtService, never()).generateAccessToken(any());

        verify(jwtService, never()).generateRefreshToken(any());
    }

    private SignedJWT createSignedJWT(String jit, String subject, Date expiryTime) {

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(jit)
                .subject(subject)
                .expirationTime(expiryTime)
                .build();

        return new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    }
}
