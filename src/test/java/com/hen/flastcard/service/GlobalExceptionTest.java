package com.hen.flastcard.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.metadata.ConstraintDescriptor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.hen.flastcard.exception.AppException;
import com.hen.flastcard.exception.ErrorCode;
import com.hen.flastcard.exception.GlobalException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionTest {

    private GlobalException globalException;

    @BeforeEach
    void initData() {
        globalException = new GlobalException();
    }

    @Test
    void handlingRuntimeException_success() {
        RuntimeException exception = new RuntimeException("Something went wrong");

        var response = globalException.handlingRuntimeException(exception);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        Assertions.assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());

        Assertions.assertThat(response.getBody().getMessage())
                .isEqualTo(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
    }

    @Test
    void handlingAppException_success() {
        AppException exception = new AppException(ErrorCode.USER_NOT_EXISTED);

        var response = globalException.handlingAppException(exception);

        Assertions.assertThat(response.getStatusCode().value())
                .isEqualTo(ErrorCode.USER_NOT_EXISTED.getHttpStatusCode().value());

        Assertions.assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED.getCode());

        Assertions.assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.USER_NOT_EXISTED.getMessage());
    }

    @Test
    void handlingAccessDeniedException_success() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        var response = globalException.handlingAccessDeniedException(exception);

        Assertions.assertThat(response.getStatusCode().value())
                .isEqualTo(ErrorCode.UNAUTHORIZED.getHttpStatusCode().value());

        Assertions.assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.UNAUTHORIZED.getCode());

        Assertions.assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.UNAUTHORIZED.getMessage());
    }

    @Test
    void handlingMethodArgumentNotValidException_validErrorCode_success() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = mock(FieldError.class);

        ConstraintViolation<?> constraintViolation = mock(ConstraintViolation.class);

        ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);

        Map<String, Object> attributes = Map.of("min", 5);

        when(exception.getBindingResult()).thenReturn(bindingResult);

        when(bindingResult.getFieldError()).thenReturn(fieldError);

        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        when(fieldError.getDefaultMessage()).thenReturn("EMAIL_REQUIRED");

        when(fieldError.unwrap(ConstraintViolation.class)).thenReturn(constraintViolation);

        when(constraintViolation.getConstraintDescriptor()).thenReturn(descriptor);

        when(descriptor.getAttributes()).thenReturn(attributes);

        var response = globalException.handlingMethodArgumentNotValidException(exception);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Assertions.assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.EMAIL_REQUIRED.getCode());

        Assertions.assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.EMAIL_REQUIRED.getMessage());
    }

    @Test
    void handlingMethodArgumentNotValidException_fieldErrorNull_fail() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);

        when(bindingResult.getFieldError()).thenReturn(null);

        var response = globalException.handlingMethodArgumentNotValidException(exception);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Assertions.assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.KEY_INVALID.getCode());

        Assertions.assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.KEY_INVALID.getMessage());
    }

    @Test
    void handlingMethodArgumentNotValidException_invalidErrorCode_fail() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = mock(FieldError.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);

        when(bindingResult.getFieldError()).thenReturn(fieldError);

        when(fieldError.getDefaultMessage()).thenReturn("ERROR_CODE_DOES_NOT_EXIST");

        var response = globalException.handlingMethodArgumentNotValidException(exception);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Assertions.assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.KEY_INVALID.getCode());

        Assertions.assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.KEY_INVALID.getMessage());
    }
}
