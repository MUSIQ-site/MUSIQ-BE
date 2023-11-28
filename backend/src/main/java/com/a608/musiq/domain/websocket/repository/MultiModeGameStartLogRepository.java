package com.a608.musiq.domain.websocket.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a608.musiq.domain.websocket.domain.log.MultiModeGameStartLog;

@Repository
public interface MultiModeGameStartLogRepository extends JpaRepository<MultiModeGameStartLog, Integer> {

	@Query("select MAX(m.startedAt) "
		+ "from MultiModeGameStartLog m "
		+ "where m.multiModeCreateGameRoomLogId = :multiModeCreateGameRoomLogId")
	Optional<LocalDateTime> findLatestStartedAtByMultiModeCreateGameRoomLogId(
		@Param("multiModeCreateGameRoomLogId") int multiModeCreateGameRoomLogId);
}
