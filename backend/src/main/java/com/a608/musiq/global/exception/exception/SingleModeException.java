package com.a608.musiq.global.exception.exception;

import com.a608.musiq.global.exception.info.SingleModeExceptionInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SingleModeException extends RuntimeException {
	private final SingleModeExceptionInfo info;
}
