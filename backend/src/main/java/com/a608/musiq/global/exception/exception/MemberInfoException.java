package com.a608.musiq.global.exception.exception;

import com.a608.musiq.global.exception.info.MemberInfoExceptionInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInfoException extends RuntimeException{
	private final MemberInfoExceptionInfo info;
}
