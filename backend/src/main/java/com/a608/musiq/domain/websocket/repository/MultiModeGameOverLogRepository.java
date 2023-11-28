package com.a608.musiq.domain.websocket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.a608.musiq.domain.websocket.domain.log.MultiModeGameOverLog;

@Repository
public interface MultiModeGameOverLogRepository extends JpaRepository<MultiModeGameOverLog, Integer> {
}
