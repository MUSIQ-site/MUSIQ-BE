package com.a608.musiq.global.exception.exception;

import com.a608.musiq.global.exception.info.GuestModeExceptionInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuestModeException extends RuntimeException {
	private final GuestModeExceptionInfo info;
}
