package com.a608.musiq.domain.music.data;

import jakarta.persistence.AttributeConverter;

public class DifficultyConverter implements AttributeConverter<Difficulty, String> {

	@Override
	public String convertToDatabaseColumn(Difficulty attribute) {
		return attribute.getValue();
	}

	@Override
	public Difficulty convertToEntityAttribute(String dbData) {
		return Difficulty.ofName(dbData);
	}
}
