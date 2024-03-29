package com.a608.musiq.domain.music.controller;

import com.a608.musiq.domain.music.dto.requestDto.AddIpInLogRequestDto;
import com.a608.musiq.domain.music.dto.responseDto.*;
import com.a608.musiq.domain.music.dto.responseDto.v2.*;
import com.a608.musiq.domain.music.dto.serviceDto.CreateRoomRequestServiceDto;
import com.a608.musiq.domain.music.service.SingleModeMusicService;
import com.a608.musiq.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
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
	private ResponseEntity<BaseResponse<RoundInfoResponseDto>> ResumePrevGameRoom(
			@RequestHeader("accessToken") String token
	) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(BaseResponse.<RoundInfoResponseDto>builder()
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
	private ResponseEntity<BaseResponse<RoundInfoResponseDto>> createRoom(
		@RequestParam("difficulty") String difficulty,
		@RequestParam("year") String year,
		@RequestHeader("accessToken") String token
	) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.<RoundInfoResponseDto>builder()
				.code(HttpStatus.OK.value())
				.data(musicService.startNewGame(CreateRoomRequestServiceDto.from(difficulty, year, token)))
				.build());
	}

	/**
	 * 재생 가능 여부 확인
	 * 
	 * @param token
	 * @return
	 */
	@GetMapping("/listencheck")
	private ResponseEntity<BaseResponse<MusicPlayCheckResponseDto>> checkMusicPlay(
			@RequestHeader("accessToken") String token
	) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(BaseResponse.<MusicPlayCheckResponseDto>builder()
						.code(HttpStatus.OK.value())
						.data(musicService.checkMusicPlay(token))
						.build());
	}

	/**
	 * 정답 채점
	 * 
	 * @param token
	 * @param answer
	 * @return
	 */
	@GetMapping("/answercheck")
	private ResponseEntity<BaseResponse<CheckAnswerResponseDto>> checkMusicAnswer(
			@RequestHeader("accessToken") String token,
			@RequestParam("answer") String answer
	) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(BaseResponse.<CheckAnswerResponseDto>builder()
						.code(HttpStatus.OK.value())
						.data(musicService.checkAnswer(token, answer))
						.build());
	}

	/**
	 * 라운드 스킵
	 *
	 * @param token
	 * @return
	 */
	@PatchMapping("/skip")
	private ResponseEntity<BaseResponse<SingleSkipResponseDto>> skipCurrentRound(
			@RequestHeader("accessToken") String token
	) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(BaseResponse.<SingleSkipResponseDto>builder()
						.code(HttpStatus.OK.value())
						.data(musicService.skipRound(token))
						.build());
	}


	/**
	 * 현재 라운드 종료
	 * 
	 * @param token
	 * @return
	 */
	@GetMapping("/roundend")
	private ResponseEntity<BaseResponse<SingleRoundEndResponseDto>> endCurrentRound(
			@RequestHeader("accessToken") String token
	) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(BaseResponse.<SingleRoundEndResponseDto>builder()
						.code(HttpStatus.OK.value())
						.data(musicService.endRound(token))
						.build());
	}

	/**
	 * 다음 라운드 정보 받기
	 *
	 * @param token
	 * @return
	 */
	@GetMapping("/nextround")
	private ResponseEntity<BaseResponse<RoundInfoResponseDto>> goNextRound(
			@RequestHeader("accessToken") String token
	) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(BaseResponse.<RoundInfoResponseDto>builder()
						.code(HttpStatus.OK.value())
						.data(musicService.nextRound(token))
						.build());
	}

	/**
	 * 게임 종료
	 * 
	 * @param token
	 * @return
	 */
	@DeleteMapping("/gameover")
	private ResponseEntity<BaseResponse<SingleGameOverResponseDto>> gameOverCurrentGame (
			@RequestHeader("accessToken") String token
	) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(BaseResponse.<SingleGameOverResponseDto>builder()
						.code(HttpStatus.OK.value())
						.data(musicService.gameOver(token))
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
		@RequestHeader("accessToken") String token,
		@RequestBody AddIpInLogRequestDto addIpInLogRequestDto
	) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.<AddIpInLogResponseDto>builder()
				.code(HttpStatus.OK.value())
				.data(musicService.addIpInLog(token, addIpInLogRequestDto))
				.build());
	}
}
