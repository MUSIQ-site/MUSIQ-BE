package com.a608.musiq.domain.util.repository;

import com.a608.musiq.domain.admin.dto.GetReportBugItem;
import com.a608.musiq.domain.admin.dto.GetReportSuggestionItem;
import com.a608.musiq.domain.util.domain.SystemReportLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemReportLogJpaRepository extends JpaRepository<SystemReportLog, Integer> {

    @Query("SELECT new com.a608.musiq.domain.admin.dto.GetReportBugItem(s.nickname, s.createdAt, s.content) " +
            "FROM SystemReportLog s " +
            "WHERE s.type = 'BUG' " +
            "ORDER BY s.createdAt")
    Page<GetReportBugItem> findBugReportsInRangeWithPagination(Pageable pageable);

    @Query("SELECT new com.a608.musiq.domain.admin.dto.GetReportSuggestionItem(s.nickname, s.createdAt, s.content) " +
            "FROM SystemReportLog s " +
            "WHERE s.type = 'SUGGESTION' " +
            "ORDER BY s.createdAt")
    Page<GetReportSuggestionItem> findSuggestionReportsInRangeWithPagination(Pageable pageable);
}
