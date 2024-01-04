package com.a608.musiq.domain.websocket.repository;

import com.a608.musiq.domain.websocket.domain.log.MultiModeModifyGameRoomInformationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MultiModeModifyGameRoomInformationLogRepository
        extends JpaRepository<MultiModeModifyGameRoomInformationLog, Integer> {

}
