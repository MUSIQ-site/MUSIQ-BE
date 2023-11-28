package com.a608.musiq.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.a608.musiq.global.common.response.BaseResponse;
import com.a608.musiq.global.exception.exception.GuestModeException;
import com.a608.musiq.global.exception.exception.MemberException;
import com.a608.musiq.global.exception.exception.MemberInfoException;
import com.a608.musiq.global.exception.exception.MultiModeException;
import com.a608.musiq.global.exception.exception.MusicException;
import com.a608.musiq.global.exception.exception.RankingException;
import com.a608.musiq.global.exception.exception.SingleModeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MemberException.class)
	public ResponseEntity<BaseResponse<MemberException>> memberExceptionHandler(MemberException exception) {
		return ResponseEntity.status(exception.getInfo().getStatus())
			.body(BaseResponse.<MemberException>builder()
				.code(exception.getInfo().getCode())
				.message(exception.getInfo().getMessage())
				.build());
	}

	@ExceptionHandler(MemberInfoException.class)
	public ResponseEntity<BaseResponse<MemberInfoException>> memberInfoExceptionHandler(MemberInfoException exception) {
		return ResponseEntity.status(exception.getInfo().getStatus())
			.body(BaseResponse.<MemberInfoException>builder()
				.code(exception.getInfo().getCode())
				.message(exception.getInfo().getMessage())
				.build());
	}

	@ExceptionHandler(MusicException.class)
	public ResponseEntity<BaseResponse<MusicException>> musicExceptionHandler(MusicException exception) {
		return ResponseEntity.status(exception.getInfo().getStatus())
			.body(BaseResponse.<MusicException>builder()
				.code(exception.getInfo().getCode())
				.message(exception.getInfo().getMessage())
				.build());
	}

	@ExceptionHandler(RankingException.class)
	public ResponseEntity<BaseResponse<RankingException>> rankingExceptionHandler(RankingException exception) {
		return ResponseEntity.status(exception.getInfo().getStatus())
			.body(BaseResponse.<RankingException>builder()
				.code(exception.getInfo().getCode())
				.message(exception.getInfo().getMessage())
				.build());
	}

	@ExceptionHandler(GuestModeException.class)
	public ResponseEntity<BaseResponse<GuestModeException>> guestModeExceptionHandler(
		GuestModeException exception) {
		return ResponseEntity.status(exception.getInfo().getStatus())
			.body(BaseResponse.<GuestModeException>builder()
				.code(exception.getInfo().getCode())
				.message(exception.getInfo().getMessage())
				.build());
	}

	@ExceptionHandler(SingleModeException.class)
	public ResponseEntity<BaseResponse<SingleModeException>> singleModeExceptionHandler(
		SingleModeException exception) {
		return ResponseEntity.status(exception.getInfo().getStatus())
			.body(BaseResponse.<SingleModeException>builder()
				.code(exception.getInfo().getCode())
				.message(exception.getInfo().getMessage())
				.build());
	}

	@ExceptionHandler(MultiModeException.class)
	public ResponseEntity<BaseResponse<MultiModeException>> multiModeExceptionHandler(
		MultiModeException exception) {
		return ResponseEntity.status(exception.getInfo().getStatus())
			.body(BaseResponse.<MultiModeException>builder()
				.code(exception.getInfo().getCode())
				.message(exception.getInfo().getMessage())
				.build());
	}
}
