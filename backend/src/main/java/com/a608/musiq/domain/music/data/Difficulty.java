package com.a608.musiq.domain.music.data;

import java.util.Arrays;

import com.a608.musiq.global.exception.exception.MusicException;
import com.a608.musiq.global.exception.info.MusicExceptionInfo;

import lombok.Getter;

@Getter
public enum Difficulty {

	EASY("EASY"),
	NORMAL("NORMAL"),
	HARD("HARD"),
	CRAZY("CRAZY");

	private final String value;

	Difficulty(String value) {
		this.value = value;
	}

	public static Difficulty ofName(String name) {
		return Arrays.stream(Difficulty.values())
			.filter(value -> value.getValue().equals(name))
			.findAny()
			.orElseThrow(() -> new MusicException(MusicExceptionInfo.INVALID_DIFFICULTY));
	}
}