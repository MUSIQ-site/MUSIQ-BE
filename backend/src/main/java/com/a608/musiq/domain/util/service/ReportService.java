package com.a608.musiq.domain.util.service;

import com.a608.musiq.domain.member.domain.Member;
import com.a608.musiq.domain.member.domain.MemberInfo;
import com.a608.musiq.domain.member.repository.MemberInfoRepository;
import com.a608.musiq.domain.member.repository.MemberRepository;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

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
}
