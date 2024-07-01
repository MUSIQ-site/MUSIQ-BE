package com.a608.musiq.domain.util.service;

import com.a608.musiq.domain.admin.dto.GetReportItem;
import com.a608.musiq.domain.admin.dto.GetSuggestionReportItem;
import com.a608.musiq.domain.admin.dto.responseDto.GetReportResponseDto;
import com.a608.musiq.domain.admin.dto.responseDto.GetSuggestionResponseDto;
import com.a608.musiq.domain.member.domain.Member;
import com.a608.musiq.domain.member.domain.MemberInfo;
import com.a608.musiq.domain.member.repository.MemberInfoRepository;
import com.a608.musiq.domain.member.repository.MemberRepository;
import com.a608.musiq.domain.util.Data.ReportType;
import com.a608.musiq.domain.util.domain.SystemReportLog;
import com.a608.musiq.domain.util.dto.requestDto.SaveSystemReportRequestDto;
import com.a608.musiq.domain.util.dto.responseDto.SaveSystemReportResponseDto;
import com.a608.musiq.domain.util.repository.SystemReportLogJpaRepository;
import com.a608.musiq.global.exception.exception.MemberException;
import com.a608.musiq.global.exception.exception.MemberInfoException;
import com.a608.musiq.global.exception.info.MemberExceptionInfo;
import com.a608.musiq.global.exception.info.MemberInfoExceptionInfo;
import com.a608.musiq.global.jwt.JwtValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final static int PAGINATION_NUMBER = 1;

    private final JwtValidator jwtValidator;
    private final MemberRepository memberRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final SystemReportLogJpaRepository systemReportLogJpaRepository;

    public SaveSystemReportResponseDto systemReporting(
            String accessToken, SaveSystemReportRequestDto saveSystemReportRequestDto) {
        UUID uuid = jwtValidator.getData(accessToken);
        Member member = memberRepository.findById(uuid).orElseThrow(() -> new MemberException(MemberExceptionInfo.NOT_FOUND_MEMBER));
        MemberInfo memberInfo = memberInfoRepository.findById(uuid).orElseThrow(() -> new MemberInfoException(MemberInfoExceptionInfo.NOT_FOUND_MEMBER_INFO));

        SystemReportLog systemReportLog = SystemReportLog.builder()
                .type(saveSystemReportRequestDto.getType())
                .loginId(member.getLoginId())
                .nickname(memberInfo.getNickname())
                .content(saveSystemReportRequestDto.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        systemReportLogJpaRepository.save(systemReportLog);

        return SaveSystemReportResponseDto.builder()
                .isSuccess(true)
                .build();
    }

    /**
     * 신고 전체 조회
     *
     * @param page
     * @param size
     * @return GetReportResponseDto
     */
    public GetReportResponseDto getReport(int page, int size) {
        long totalAmount = systemReportLogJpaRepository.count();
        Pageable pageable = PageRequest.of(page - PAGINATION_NUMBER, size);
        List<GetReportItem> reportItems =
                systemReportLogJpaRepository.findReportsInRangeWithPagination(pageable).getContent();

        return GetReportResponseDto.from(totalAmount, reportItems);
    }

    /**
     * 건의 조회
     *
     * @param page
     * @param size
     * @return GetSuggestionResponseDto
     */
    public GetSuggestionResponseDto getSuggestionReport(int page, int size) {
        long suggestionReportAmount = systemReportLogJpaRepository.countByType(ReportType.Suggestion);

        Pageable pageable = PageRequest.of(page - PAGINATION_NUMBER, size);
        List<GetSuggestionReportItem> suggestionReportItems =
                systemReportLogJpaRepository.findSuggestionReportsInRangeWithPagination(pageable).getContent();

        return GetSuggestionResponseDto.from(suggestionReportAmount, suggestionReportItems);
    }


}
