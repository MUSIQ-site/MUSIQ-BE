package com.a608.musiq.global.exception.exception;

import com.a608.musiq.global.exception.info.MemberExceptionInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberException extends RuntimeException{
	private final MemberExceptionInfo info;
}
