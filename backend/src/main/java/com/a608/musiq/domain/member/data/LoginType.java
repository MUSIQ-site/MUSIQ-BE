package com.a608.musiq.domain.member.data;

import java.util.Arrays;

import com.a608.musiq.global.exception.exception.MemberException;
import com.a608.musiq.global.exception.info.MemberExceptionInfo;

import lombok.Getter;

@Getter
public enum LoginType {
	SOCIAL("SOCIAL"),
	SIMPLE("SIMPLE");

	private final String type;

	LoginType(String type) {
		this.type = type;
	}

	public static LoginType ofType(String type) {
		return Arrays.stream(LoginType.values())
			.filter(value -> value.getType().equals(type))
			.findAny()
			.orElseThrow(() -> new MemberException(MemberExceptionInfo.INVALID_LOGIN_TYPE));
	}
}
