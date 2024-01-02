package com.a608.musiq.domain.music.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class SingleModeRoomManagerV2 {
	private Map<UUID, SingleGameRoom> rooms;

	public SingleModeRoomManagerV2() {
		this.rooms = new HashMap<>();
	}

	public void addRoom(UUID memberId, SingleGameRoom room) {
		rooms.put(memberId, room);
	}

}
