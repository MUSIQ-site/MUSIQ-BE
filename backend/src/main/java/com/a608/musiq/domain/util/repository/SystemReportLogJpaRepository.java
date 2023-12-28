package com.a608.musiq.domain.util.repository;

import com.a608.musiq.domain.util.domain.SystemReportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemReportLogJpaRepository extends JpaRepository<SystemReportLog, Integer> {
}
