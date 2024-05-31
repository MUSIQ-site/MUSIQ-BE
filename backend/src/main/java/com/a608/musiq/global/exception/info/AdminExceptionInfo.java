package com.a608.musiq.global.exception.info;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AdminExceptionInfo {
    INVALID_TYPE(HttpStatus.NOT_FOUND, 1700, "유효하지 않은 타입입니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;

    AdminExceptionInfo(HttpStatus status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
