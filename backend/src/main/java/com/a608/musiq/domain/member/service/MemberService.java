package com.a608.musiq.domain.member.service;

import com.a608.musiq.domain.member.dto.requestDto.ReissueTokenRequestDto;
import com.a608.musiq.domain.member.dto.requestDto.VisitRequestDto;
import com.a608.musiq.domain.member.dto.requestDto.LoginRequestDto;
import com.a608.musiq.domain.member.dto.responseDto.LoginResponseDto;
import com.a608.musiq.domain.member.dto.responseDto.LogoutResponseDto;
import com.a608.musiq.domain.member.dto.responseDto.ReissueTokenResponseDto;
import com.a608.musiq.domain.member.dto.responseDto.ValidateDuplicatedLoginIdResponseDto;
import com.a608.musiq.domain.member.dto.responseDto.ValidateDuplicatedNicknameResponseDto;
import com.a608.musiq.domain.member.dto.responseDto.VisitResponseDto;

import com.a608.musiq.domain.member.dto.requestDto.JoinRequestDto;
import com.a608.musiq.domain.member.dto.responseDto.JoinResponseDto;

public interface MemberService {
	VisitResponseDto visit(VisitRequestDto visitRequestDto);

	JoinResponseDto signUp(JoinRequestDto joinRequestDto);

	LoginResponseDto login(LoginRequestDto loginRequestDto);

	LogoutResponseDto logout(String token);

	ValidateDuplicatedLoginIdResponseDto validateDuplicatedLoginId(String loginId);

	ValidateDuplicatedNicknameResponseDto validateDuplicatedNickname(String nickname);

	ReissueTokenResponseDto reissueToken(ReissueTokenRequestDto reissueTokenRequestDto);
}
