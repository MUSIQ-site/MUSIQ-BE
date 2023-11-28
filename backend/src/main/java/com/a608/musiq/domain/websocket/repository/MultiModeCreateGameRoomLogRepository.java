package com.a608.musiq.domain.websocket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.a608.musiq.domain.websocket.domain.log.MultiModeCreateGameRoomLog;

@Repository
public interface MultiModeCreateGameRoomLogRepository extends JpaRepository<MultiModeCreateGameRoomLog, Integer> {

}
