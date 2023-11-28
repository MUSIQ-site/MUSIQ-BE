package com.a608.musiq.domain.websocket.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUserInfoItemDto {
	private String nicknames;
	private String exps;
}
