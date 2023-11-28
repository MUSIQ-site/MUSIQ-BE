package com.a608.musiq.global.exception.exception;

import com.a608.musiq.global.exception.info.MultiModeExceptionInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MultiModeException extends RuntimeException{
    private final MultiModeExceptionInfo info;
}
