package com.a608.musiq.domain.music.dto.queryDto;

import lombok.Getter;

@Getter
public class FindAnswerDto {

	private String title;

	public FindAnswerDto(String title) {
		this.title = title;
	}
}
