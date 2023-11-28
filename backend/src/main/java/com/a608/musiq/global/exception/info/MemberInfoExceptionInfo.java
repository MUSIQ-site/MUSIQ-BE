package com.a608.musiq.global.exception.info;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum MemberInfoExceptionInfo {
	NOT_FOUND_MEMBER_INFO(HttpStatus.NOT_FOUND, 1100, "사용자 정보룰 찾을 수 없습니다.");

	private final HttpStatus status;
	private final Integer code;
	private final String message;

	MemberInfoExceptionInfo(HttpStatus status, Integer code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
