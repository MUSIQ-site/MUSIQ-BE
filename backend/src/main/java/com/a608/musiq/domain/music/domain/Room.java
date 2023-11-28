package com.a608.musiq.domain.music.domain;

import java.util.List;

import com.a608.musiq.domain.music.data.Difficulty;
import com.a608.musiq.global.exception.exception.MusicException;
import com.a608.musiq.global.exception.info.MusicExceptionInfo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Room {
	private static final int ROUND_START_NUMBER = 0;

	private List<Music> musicList;
	private Integer round;
	private Difficulty difficulty;

	public static Room from(List<Music> musicList, Difficulty difficulty) {
		return Room.builder()
			.musicList(musicList)
			.round(ROUND_START_NUMBER)
			.difficulty(difficulty)
			.build();
	}

	public void addRound(Integer round) {
		if (this.round != round) {
			throw new MusicException(MusicExceptionInfo.INVALID_ROUND);
		}
		this.round = round + 1;
	}

}
