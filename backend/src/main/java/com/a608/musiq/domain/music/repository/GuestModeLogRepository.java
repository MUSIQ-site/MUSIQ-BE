package com.a608.musiq.domain.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.a608.musiq.domain.music.domain.log.GuestModeLog;

@Repository
public interface GuestModeLogRepository extends JpaRepository<GuestModeLog, Integer> {
}
