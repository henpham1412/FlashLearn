package com.hen.flastcard.controller;

import java.text.ParseException;
import java.time.Duration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import com.hen.flastcard.dto.request.AuthenticationRequest;
import com.hen.flastcard.dto.response.ApiResponse;
import com.hen.flastcard.dto.response.AuthenticationResponse;
import com.hen.flastcard.dto.response.RefreshResponse;
import com.hen.flastcard.service.AuthenticationService;
import com.hen.flastcard.service.JwtService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    JwtService jwtService;

    @NonFinal
    @Value("${app.cookie.secure}")
    protected boolean cookieSecure;

    @NonFinal
    @Value("${app.cookie.name}")
    protected String cookieName;

    @PostMapping("/auth/login")
    public ApiResponse<AuthenticationResponse> login(
            @RequestBody @Valid AuthenticationRequest request, HttpServletResponse response) {
        var authenticationResponse = authenticationService.authenticate(request);

        ResponseCookie refreshCookie = ResponseCookie.from(cookieName, authenticationResponse.getRefreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("lax")
                .path("/")
                .maxAge(Duration.ofSeconds(120))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ApiResponse.<AuthenticationResponse>builder()
                .result(AuthenticationResponse.builder()
                        .accessToken(authenticationResponse.getAccessToken())
                        .authenticated(true)
                        .build())
                .build();
    }

    @PostMapping("/auth/logout")
    ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response)
            throws ParseException, JOSEException {
        String refreshToken = jwtService.getRefreshToken(request);
        authenticationService.logout(refreshToken);
        ResponseCookie deleteCookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        return ApiResponse.<Void>builder().build();
    }

    //    @PostMapping("/auth/refresh")
    //    ApiResponse<RefreshResponse> refresh(@RequestBody RefreshRequest request) throws ParseException, JOSEException
    // {
    //        return ApiResponse.<RefreshResponse>builder()
    //                .result(authenticationService.refresh(request))
    //                .build();
    //    }

    @PostMapping("/auth/refresh")
    ApiResponse<RefreshResponse> refresh(HttpServletRequest request, HttpServletResponse response)
            throws ParseException, JOSEException {
        String refreshToken = jwtService.getRefreshToken(request);
        var refreshResponse = authenticationService.refresh(refreshToken);
        // set new refresh token
        ResponseCookie refreshCookie = ResponseCookie.from(cookieName, refreshResponse.getRefreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("lax")
                .path("/")
                .maxAge(Duration.ofSeconds(120))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ApiResponse.<RefreshResponse>builder()
                .result(RefreshResponse.builder()
                        .accessToken(refreshResponse.getAccessToken())
                        .build())
                .build();
    }
}
