package com.a608.musiq.global.exception.exception;

import com.a608.musiq.global.exception.info.MemberExceptionInfo;
import com.a608.musiq.global.exception.info.MusicExceptionInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MusicException extends RuntimeException{
	private final MusicExceptionInfo info;
}
