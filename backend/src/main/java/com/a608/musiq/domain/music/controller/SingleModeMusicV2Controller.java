package com.a608.musiq.domain.music.controller;

import com.a608.musiq.domain.music.dto.requestDto.AddIpInLogRequestDto;
import com.a608.musiq.domain.music.dto.responseDto.*;
import com.a608.musiq.domain.music.dto.responseDto.v2.CheckPrevGameResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.v2.DeletePrevGameResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.v2.GameStartResponseDto;
import com.a608.musiq.domain.music.dto.serviceDto.CreateRoomRequestServiceDto;
import com.a608.musiq.domain.music.service.GuestModeMusicService;
import com.a608.musiq.domain.music.service.SingleModeMusicService;
import com.a608.musiq.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/music/single/v2")
public class SingleModeMusicV2Controller {

	private final SingleModeMusicService musicService;

	/**
	 * 이전 진행 중인 게임 존재 유무 확인
	 *
	 * @param token
	 * @return
	 */
	@GetMapping("/pastgame")
	private ResponseEntity<BaseResponse<CheckPrevGameResponseDto>> checkPrevGameRoom(
			@RequestHeader("accessToken") String token
	) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(BaseResponse.<CheckPrevGameResponseDto>builder()
						.code(HttpStatus.OK.value())
						.data(musicService.checkPrevGame(token))
						.build());
	}

	/**
	 * 이전 진행 중인 게임 삭제
	 * 
	 * @param token
	 * @return
	 */
	@DeleteMapping("/pastgame")
	private ResponseEntity<BaseResponse<DeletePrevGameResponseDto>> deletePrevGameRoom(
			@RequestHeader("accessToken") String token
	) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(BaseResponse.<DeletePrevGameResponseDto>builder()
						.code(HttpStatus.OK.value())
						.data(musicService.deletePrevGame(token))
						.build());
	}

	/**
	 * 이전 진행 중인 게임 이어하기
	 * 
	 * @param token
	 * @return
	 */
	@GetMapping("/resumption")
	private ResponseEntity<BaseResponse<GameStartResponseDto>> ResumePrevGameRoom(
			@RequestHeader("accessToken") String token
	) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(BaseResponse.<GameStartResponseDto>builder()
						.code(HttpStatus.OK.value())
						.data(musicService.resumePrevGame(token))
						.build());
	}

	/**
	 * 싱글 모드 방 생성
	 *
	 * @param difficulty
	 * @param year
	 * @return
	 */
	@PostMapping("/room")
	private ResponseEntity<BaseResponse<GameStartResponseDto>> createRoom(
		@RequestParam("difficulty") String difficulty,
		@RequestParam("year") String year,
		@RequestHeader("accessToken") String token
	) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.<GameStartResponseDto>builder()
				.code(HttpStatus.OK.value())
				.data(musicService.startNewGame(CreateRoomRequestServiceDto.from(difficulty, year, token)))
				.build());
	}

	/**
	 * 로그에 ip 추가
	 *
	 * @param addIpInLogRequestDto
	 * @see AddIpInLogResponseDto
	 * @return ResponseEntity<BaseResponse < AddIpInLogResponseDto>>
	 */
	@PatchMapping("/log")
	private ResponseEntity<BaseResponse<AddIpInLogResponseDto>> addIpInLog(
		@RequestBody AddIpInLogRequestDto addIpInLogRequestDto
	) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.<AddIpInLogResponseDto>builder()
				.code(HttpStatus.OK.value())
				.data(musicService.addIpInLog(addIpInLogRequestDto))
				.build());
	}
}
