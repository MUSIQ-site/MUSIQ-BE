package com.a608.musiq.domain.music.dto.responseDto;

import com.a608.musiq.domain.music.domain.Music;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GradeAnswerResponseDto {
	private Boolean isCorrect;
	private int round;
	private String title;
	private String singer;

	public static GradeAnswerResponseDto from(Boolean isCorrect, int round, Music music) {
		return GradeAnswerResponseDto.builder()
			.isCorrect(isCorrect)
			.round(round)
			.title(music.getTitle())
			.singer(music.getSinger())
			.build();
	}

}
