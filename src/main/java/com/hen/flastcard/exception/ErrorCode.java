package com.hen.flastcard.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    KEY_INVALID(1001, "key not found", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "user has been existed", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception" , HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_INVALID(1003, "username must be at least {min} characters" , HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "password must be at least {min} characters" , HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "user has not been existed" , HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "unauthenticated" , HttpStatus.UNAUTHORIZED),
    INVALID_DOB(1007, "your age must be at least {min}" , HttpStatus.BAD_REQUEST),
    USERNAME_REQUIRED(1008, "you must have a username", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1009, "you must have a password", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(1009, "you must have a email", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1010, "your email is invalid", HttpStatus.BAD_REQUEST)
    ;
    int code;
    String message;
    HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

}
