package com.a608.musiq.domain.music.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class SingleModeRoomManagerV1 {
	private Map<Integer, Room> rooms;

	public SingleModeRoomManagerV1() {
		this.rooms = new HashMap<>();
	}

	public void addRoom(int roomId, Room room) {
		rooms.put(roomId, room);
	}

}
