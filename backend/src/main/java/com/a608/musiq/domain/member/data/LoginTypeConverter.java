package com.a608.musiq.domain.member.data;


import jakarta.persistence.AttributeConverter;

public class LoginTypeConverter implements AttributeConverter<LoginType, String> {

	@Override
	public String convertToDatabaseColumn(LoginType attribute) {
		return attribute.getType();
	}

	@Override
	public LoginType convertToEntityAttribute(String dbData) {
		return LoginType.ofType(dbData);
	}
}
