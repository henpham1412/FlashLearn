package com.hen.flastcard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hen.flastcard.dto.request.AuthenticationRequest;
import com.hen.flastcard.dto.response.AuthenticationResponse;
import com.hen.flastcard.dto.response.RefreshResponse;
import com.hen.flastcard.service.AuthenticationService;
import com.hen.flastcard.service.JwtService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;

    private AuthenticationRequest authenticationRequest;
    private AuthenticationResponse authenticationResponse;
    private RefreshResponse refreshResponse;
    private static final String CSRF_TOKEN = "test-csrf-token";
    @BeforeEach
    void initData() {
        authenticationRequest = AuthenticationRequest.builder()
                .email("john@gmail.com")
                .password("12345678")
                .build();

        authenticationResponse = AuthenticationResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .authenticated(true)
                .build();

        refreshResponse = RefreshResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .build();
    }

    @Test
    void login_validRequest_success() throws Exception {
        when(authenticationService.authenticate(authenticationRequest)).thenReturn(authenticationResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.accessToken").value("access-token"))
                .andExpect(jsonPath("$.result.authenticated").value(true))
                .andExpect(header().string(
                                HttpHeaders.SET_COOKIE, Matchers.containsString("refresh_token=refresh-token")));

        verify(authenticationService).authenticate(authenticationRequest);
    }

    @Test
    void logout_validRequest_success() throws Exception {
        when(jwtService.getRefreshToken(any(HttpServletRequest.class))).thenReturn("refresh-token");

        doNothing().when(authenticationService).logout("refresh-token");

        mockMvc.perform(post("/api/auth/logout")
                        .cookie(
                                new Cookie("refresh_token", "refresh-token"),
                                new Cookie("XSRF-TOKEN", CSRF_TOKEN)
                        )
                        .header("X-XSRF-TOKEN", CSRF_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(header().string(
                        HttpHeaders.SET_COOKIE,
                        Matchers.containsString("refresh_token=")));

        verify(jwtService).getRefreshToken(any(HttpServletRequest.class));

        verify(authenticationService).logout("refresh-token");
    }

    @Test
    void refresh_validRequest_success() throws Exception {
        when(jwtService.getRefreshToken(any(HttpServletRequest.class))).thenReturn("old-refresh-token");

        when(authenticationService.refresh("old-refresh-token")).thenReturn(refreshResponse);

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(
                                new Cookie("refresh_token", "old-refresh-token"),
                                new Cookie("XSRF-TOKEN", CSRF_TOKEN)
                        )
                        .header("X-XSRF-TOKEN", CSRF_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.accessToken").value("new-access-token"))
                .andExpect(header().string(
                        HttpHeaders.SET_COOKIE,
                        Matchers.containsString("refresh_token=new-refresh-token")));

        verify(jwtService).getRefreshToken(any(HttpServletRequest.class));

        verify(authenticationService).refresh("old-refresh-token");
    }

    @Test
    void refresh_missingCsrfToken_forbidden() throws Exception {
        when(jwtService.getRefreshToken(any(HttpServletRequest.class)))
                .thenReturn("old-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refresh_token", "old-refresh-token")))
                .andExpect(status().isForbidden());

        verify(authenticationService, never()).refresh(any());
    }

    @Test
    void logout_invalidCsrfToken_forbidden() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .cookie(
                                new Cookie("refresh_token", "refresh-token"),
                                new Cookie("XSRF-TOKEN", "correct-token")
                        )
                        .header("X-XSRF-TOKEN", "wrong-token"))
                .andExpect(status().isForbidden());

        verify(authenticationService, never()).logout(any());
    }
}
