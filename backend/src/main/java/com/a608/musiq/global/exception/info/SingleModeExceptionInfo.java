package com.a608.musiq.global.exception.info;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum SingleModeExceptionInfo {
	NOT_FOUND_LOG(HttpStatus.NOT_FOUND, 1500, "게임방을 찾을 수 없습니다.");

	private final HttpStatus status;
	private final Integer code;
	private final String message;

	SingleModeExceptionInfo(HttpStatus status, Integer code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
