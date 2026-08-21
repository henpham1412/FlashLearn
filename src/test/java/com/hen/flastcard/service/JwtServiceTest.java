package com.hen.flastcard.service;

import com.hen.flastcard.entity.Permission;
import com.hen.flastcard.entity.Role;
import com.hen.flastcard.entity.User;
import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.exception.ErrorCode;
import com.hen.flastcard.repository.InvalidatedTokenRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    private User user;
    private Role userRole;
    private Permission permission;

    private final String signerKey =
            "1234567890123456789012345678901234567890123456789012345678901234";

    private final long validDuration = 3600;
    private final long refreshableDuration = 86400;
    private final String cookieName = "refreshToken";

    @BeforeEach
    void initData() {
        ReflectionTestUtils.setField(
                jwtService,
                "SIGNER_KEY",
                signerKey
        );

        ReflectionTestUtils.setField(
                jwtService,
                "VALID_DURATION",
                validDuration
        );

        ReflectionTestUtils.setField(
                jwtService,
                "REFRESHABLE_DURATION",
                refreshableDuration
        );

        ReflectionTestUtils.setField(
                jwtService,
                "cookieName",
                cookieName
        );

        permission = Permission.builder()
                .name("READ")
                .build();

        userRole = Role.builder()
                .name("USER")
                .permissions(Set.of(permission))
                .build();

        user = User.builder()
                .id(1L)
                .username("john")
                .email("john@gmail.com")
                .roles(Set.of(userRole))
                .build();
    }

    @Test
    void generateAccessToken_valid_success() throws Exception {
        var token = jwtService.generateAccessToken(user);

        Assertions.assertThat(token)
                .isNotBlank();

        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        Assertions.assertThat(claims.getSubject())
                .isEqualTo(user.getEmail());

        Assertions.assertThat(claims.getIssuer())
                .isEqualTo("com.hen");

        Assertions.assertThat(claims.getJWTID())
                .isNotBlank();

        Assertions.assertThat(claims.getClaim("scope"))
                .isEqualTo("ROLE_USER READ");

        Assertions.assertThat(claims.getExpirationTime())
                .isAfter(claims.getIssueTime());

        Assertions.assertThat(
                claims.getExpirationTime().getTime()
                        - claims.getIssueTime().getTime()
        ).isBetween(
                validDuration * 1000 - 1000,
                validDuration * 1000 + 1000
        );

        Assertions.assertThat(
                signedJWT.verify(
                        new MACVerifier(signerKey.getBytes())
                )
        ).isTrue();
    }

    @Test
    void generateAccessToken_withoutRoles_success() throws Exception {
        user.setRoles(Set.of());

        var token = jwtService.generateAccessToken(user);

        SignedJWT signedJWT = SignedJWT.parse(token);

        Assertions.assertThat(
                signedJWT.getJWTClaimsSet().getClaim("scope")
        ).isEqualTo("");

        Assertions.assertThat(
                signedJWT.verify(
                        new MACVerifier(signerKey.getBytes())
                )
        ).isTrue();
    }

    @Test
    void generateRefreshToken_valid_success() throws Exception {
        var token = jwtService.generateRefreshToken(user);

        Assertions.assertThat(token)
                .isNotBlank();

        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        Assertions.assertThat(claims.getSubject())
                .isEqualTo(user.getEmail());

        Assertions.assertThat(claims.getIssuer())
                .isEqualTo("com.hen");

        Assertions.assertThat(claims.getJWTID())
                .isNotBlank();

        Assertions.assertThat(claims.getClaim("type"))
                .isEqualTo("refresh");

        Assertions.assertThat(claims.getExpirationTime())
                .isAfter(claims.getIssueTime());

        Assertions.assertThat(
                signedJWT.verify(
                        new MACVerifier(signerKey.getBytes())
                )
        ).isTrue();
    }

    @Test
    void generateAccessToken_roleWithoutPermissions_success() throws Exception {
        Role roleWithoutPermissions = Role.builder()
                .name("USER")
                .permissions(Set.of())
                .build();

        user.setRoles(Set.of(roleWithoutPermissions));

        var token = jwtService.generateAccessToken(user);

        SignedJWT signedJWT = SignedJWT.parse(token);

        Assertions.assertThat(
                signedJWT.getJWTClaimsSet().getClaim("scope")
        ).isEqualTo("ROLE_USER");

        Assertions.assertThat(
                signedJWT.verify(
                        new MACVerifier(signerKey.getBytes())
                )
        ).isTrue();
    }

    @Test
    void verifyAccessToken_valid_success() {
        var token = jwtService.generateAccessToken(user);

        when(invalidatedTokenRepository.existsById(anyString()))
                .thenReturn(false);

        assertDoesNotThrow(
                () -> jwtService.verifyAccessToken(token)
        );

        verify(invalidatedTokenRepository)
                .existsById(anyString());
    }

    @Test
    void verifyAccessToken_expired_fail() throws Exception {
        String token = createToken(
                user.getEmail(),
                "access-jit",
                new Date(System.currentTimeMillis() - 1000),
                null
        );

        var exception = assertThrows(
                AppException.class,
                () -> jwtService.verifyAccessToken(token)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHENTICATED);

        verifyNoInteractions(invalidatedTokenRepository);
    }

    @Test
    void verifyAccessToken_invalidSignature_fail() throws Exception {
        String token = createToken(
                user.getEmail(),
                "access-jit",
                new Date(System.currentTimeMillis() + 60_000),
                null
        );

        ReflectionTestUtils.setField(
                jwtService,
                "SIGNER_KEY",
                "9999999999999999999999999999999999999999999999999999999999999999"
        );

        var exception = assertThrows(
                AppException.class,
                () -> jwtService.verifyAccessToken(token)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHENTICATED);

        verifyNoInteractions(invalidatedTokenRepository);
    }

    @Test
    void generateAccessToken_invalidSignerKey_fail() {
        ReflectionTestUtils.setField(
                jwtService,
                "SIGNER_KEY",
                "short-key"
        );

        var exception = assertThrows(
                RuntimeException.class,
                () -> jwtService.generateAccessToken(user)
        );

        Assertions.assertThat(exception.getCause())
                .isInstanceOf(JOSEException.class);
    }

    @Test
    void verifyAccessToken_invalidated_fail() throws Exception {
        var token = jwtService.generateAccessToken(user);

        SignedJWT signedJWT = SignedJWT.parse(token);
        String jit = signedJWT.getJWTClaimsSet().getJWTID();

        when(invalidatedTokenRepository.existsById(jit))
                .thenReturn(true);

        var exception = assertThrows(
                AppException.class,
                () -> jwtService.verifyAccessToken(token)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHENTICATED);

        verify(invalidatedTokenRepository)
                .existsById(jit);
    }

    @Test
    void verifyRefreshToken_valid_success() throws Exception {
        var token = jwtService.generateRefreshToken(user);

        when(invalidatedTokenRepository.existsById(anyString()))
                .thenReturn(false);

        SignedJWT result =
                jwtService.verifyRefreshToken(token);

        Assertions.assertThat(result)
                .isNotNull();

        Assertions.assertThat(
                result.getJWTClaimsSet().getSubject()
        ).isEqualTo(user.getEmail());

        Assertions.assertThat(
                result.getJWTClaimsSet().getClaim("type")
        ).isEqualTo("refresh");

        verify(invalidatedTokenRepository)
                .existsById(anyString());
    }

    @Test
    void verifyRefreshToken_expired_fail() throws Exception {
        String token = createToken(
                user.getEmail(),
                "refresh-jit",
                new Date(System.currentTimeMillis() - 1000),
                "refresh"
        );

        var exception = assertThrows(
                AppException.class,
                () -> jwtService.verifyRefreshToken(token)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHENTICATED);

        verifyNoInteractions(invalidatedTokenRepository);
    }

    @Test
    void verifyRefreshToken_wrongType_fail() throws Exception {
        String token = createToken(
                user.getEmail(),
                "wrong-type-jit",
                new Date(System.currentTimeMillis() + 60_000),
                "access"
        );

        var exception = assertThrows(
                AppException.class,
                () -> jwtService.verifyRefreshToken(token)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHENTICATED);

        verifyNoInteractions(invalidatedTokenRepository);
    }

    @Test
    void verifyRefreshToken_invalidated_fail() throws Exception {
        var token = jwtService.generateRefreshToken(user);

        SignedJWT signedJWT = SignedJWT.parse(token);
        String jit = signedJWT.getJWTClaimsSet().getJWTID();

        when(invalidatedTokenRepository.existsById(jit))
                .thenReturn(true);

        var exception = assertThrows(
                AppException.class,
                () -> jwtService.verifyRefreshToken(token)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHENTICATED);

        verify(invalidatedTokenRepository)
                .existsById(jit);
    }

    @Test
    void verifyRefreshToken_invalidSignature_fail() throws Exception {
        String token = createToken(
                user.getEmail(),
                "refresh-jit",
                new Date(System.currentTimeMillis() + 60_000),
                "refresh"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "SIGNER_KEY",
                "9999999999999999999999999999999999999999999999999999999999999999"
        );

        var exception = assertThrows(
                AppException.class,
                () -> jwtService.verifyRefreshToken(token)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHENTICATED);

        verifyNoInteractions(invalidatedTokenRepository);
    }

    @Test
    void getRefreshToken_valid_success() {
        HttpServletRequest request =
                mock(HttpServletRequest.class);

        Cookie refreshCookie =
                new Cookie(cookieName, "refresh-token-value");

        when(request.getCookies())
                .thenReturn(new Cookie[]{refreshCookie});

        var result = jwtService.getRefreshToken(request);

        Assertions.assertThat(result)
                .isEqualTo("refresh-token-value");
    }

    @Test
    void getRefreshToken_noCookies_fail() {
        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getCookies())
                .thenReturn(null);

        var exception = assertThrows(
                AppException.class,
                () -> jwtService.getRefreshToken(request)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHENTICATED);
    }

    @Test
    void getRefreshToken_cookieNotFound_fail() {
        HttpServletRequest request =
                mock(HttpServletRequest.class);

        Cookie otherCookie =
                new Cookie("other-cookie", "some-value");

        when(request.getCookies())
                .thenReturn(new Cookie[]{otherCookie});

        var exception = assertThrows(
                AppException.class,
                () -> jwtService.getRefreshToken(request)
        );

        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHENTICATED);
    }

    private String createToken(
            String subject,
            String jit,
            Date expirationTime,
            String type
    ) throws JOSEException {

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("com.hen")
                .issueTime(new Date())
                .expirationTime(expirationTime)
                .jwtID(jit);

        if (type != null) {
            builder.claim("type", type);
        }

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS512),
                builder.build()
        );

        signedJWT.sign(
                new MACSigner(signerKey.getBytes())
        );

        return signedJWT.serialize();
    }
}