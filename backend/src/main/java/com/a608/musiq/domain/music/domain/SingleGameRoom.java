package com.a608.musiq.domain.music.domain;

import com.a608.musiq.domain.music.data.Difficulty;
import com.a608.musiq.global.exception.exception.MusicException;
import com.a608.musiq.global.exception.info.MusicExceptionInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SingleGameRoom {
	private static final int ROUND_START_NUMBER = 1;
	private static final int TRY_NUM_START_NUMBER = 3;
	private static final int LISTEN_NUM_START_NUMBER = 3;
	private static final int LIFE_START_NUMBER = 3;
	private static final boolean IS_ROUND_ENDED_START = false;
	private static final int ROUND_OVER_TRY_NUM = 0;

	// 게임 방 ID
	private Integer roomId;

	// 난이도
	private Difficulty difficulty;

	// 연도
	private String year;

	// 라운드
	private Integer round;

	// 목숨
	private Integer life;

	// 듣기 횟수
	private Integer listenNum;

	// 시도 횟수
	private Integer tryNum;

	// 라운드 종료 여부
	private Boolean isRoundEnded;

	// 문제 리스트
	private List<Music> musicList;

	public static SingleGameRoom from(List<Music> musicList, Difficulty difficulty, String year, Integer roomId) {
		return SingleGameRoom.builder()
			.roomId(roomId)
			.difficulty(difficulty)
			.year(year)
			.round(ROUND_START_NUMBER)
			.life(LIFE_START_NUMBER)
			.listenNum(LISTEN_NUM_START_NUMBER)
			.tryNum(TRY_NUM_START_NUMBER)
			.isRoundEnded(IS_ROUND_ENDED_START)
			.musicList(musicList)
			.build();
	}

	public void goNextRound() {
		this.round++;
		this.listenNum = LISTEN_NUM_START_NUMBER;
		this.tryNum = TRY_NUM_START_NUMBER;
		isRoundEnded = IS_ROUND_ENDED_START;
	}

	public void minusListenNum() {
		this.listenNum--;
	}

	public void minusLife() {
		this.life--;
	}

	public void minusTryNum() {
		this.tryNum--;
		if(this.tryNum == ROUND_OVER_TRY_NUM) {
			this.isRoundEnded = true;
			this.life--;
		}
	}

	public void roundEnd() {
		this.isRoundEnded = true;
	}

}
