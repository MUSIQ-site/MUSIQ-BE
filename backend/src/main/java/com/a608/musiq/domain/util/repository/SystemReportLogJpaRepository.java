package com.a608.musiq.domain.util.repository;

import com.a608.musiq.domain.admin.dto.GetBugReportItem;
import com.a608.musiq.domain.admin.dto.GetReportItem;
import com.a608.musiq.domain.admin.dto.GetSuggestionReportItem;
import com.a608.musiq.domain.util.Data.ReportType;
import com.a608.musiq.domain.util.domain.SystemReportLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemReportLogJpaRepository extends JpaRepository<SystemReportLog, Integer> {

    long count();

    long countByType(ReportType type);

    @Query("SELECT new com.a608.musiq.domain.admin.dto.GetReportItem(s.nickname, s.createdAt, s.content) " +
            "FROM SystemReportLog s " +
            "ORDER BY s.createdAt")
    Page<GetReportItem> findReportsInRangeWithPagination(Pageable pageable);

    @Query("SELECT new com.a608.musiq.domain.admin.dto.GetBugReportItem(s.nickname, s.createdAt, s.content) " +
            "FROM SystemReportLog s " +
            "WHERE s.type = 'BUG' " +
            "ORDER BY s.createdAt")
    Page<GetBugReportItem> findBugReportsInRangeWithPagination(Pageable pageable);

    @Query("SELECT new com.a608.musiq.domain.admin.dto.GetSuggestionReportItem(s.nickname, s.createdAt, s.content) " +
            "FROM SystemReportLog s " +
            "WHERE s.type = 'SUGGESTION' " +
            "ORDER BY s.createdAt")
    Page<GetSuggestionReportItem> findSuggestionReportsInRangeWithPagination(Pageable pageable);
}
