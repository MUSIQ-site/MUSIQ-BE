package com.a608.musiq.global.exception.exception;

import com.a608.musiq.global.exception.info.AdminExceptionInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminException extends RuntimeException {
    private final AdminExceptionInfo info;
}
