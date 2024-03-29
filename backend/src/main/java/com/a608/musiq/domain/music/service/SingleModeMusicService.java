package com.a608.musiq.domain.music.service;

import com.a608.musiq.domain.music.dto.requestDto.AddIpInLogRequestDto;
import com.a608.musiq.domain.music.dto.responseDto.*;
import com.a608.musiq.domain.music.dto.responseDto.v2.*;
import com.a608.musiq.domain.music.dto.serviceDto.CreateRoomRequestServiceDto;

public interface SingleModeMusicService {

    AddIpInLogResponseDto addIpInLog(String token, AddIpInLogRequestDto addIpInLogRequestDto);

    // 진행 중인 게임 체크
    CheckPrevGameResponseDto checkPrevGame(String token);

    // 진행 중인 게임 삭제
    DeletePrevGameResponseDto deletePrevGame(String token);

    // 진행 중인 게임 이어하기
    RoundInfoResponseDto resumePrevGame(String token);

    // 새로운 게임 시작하기
    RoundInfoResponseDto startNewGame(CreateRoomRequestServiceDto createRoomRequestServiceDto);

    // 노래 재생 가능 여부 확인
    MusicPlayCheckResponseDto checkMusicPlay(String token);

    // 정답 채점
    CheckAnswerResponseDto checkAnswer(String token, String answer);

    // 스킵
    SingleSkipResponseDto skipRound(String token);

    // 라운드 종료
    SingleRoundEndResponseDto endRound(String token);

    // 다음 라운드
    RoundInfoResponseDto nextRound(String token);
    
    // 게임 종료
    SingleGameOverResponseDto gameOver(String token);
}
