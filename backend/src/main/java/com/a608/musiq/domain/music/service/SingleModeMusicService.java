package com.a608.musiq.domain.music.service;

import com.a608.musiq.domain.music.dto.requestDto.AddIpInLogRequestDto;
import com.a608.musiq.domain.music.dto.responseDto.*;
import com.a608.musiq.domain.music.dto.responseDto.v2.CheckPrevGameResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.v2.DeletePrevGameResponseDto;
import com.a608.musiq.domain.music.dto.responseDto.v2.GameStartResponseDto;
import com.a608.musiq.domain.music.dto.serviceDto.CreateRoomRequestServiceDto;

public interface SingleModeMusicService {

    AddIpInLogResponseDto addIpInLog(AddIpInLogRequestDto addIpInLogRequestDto);

    // 진행 중인 게임 체크
    CheckPrevGameResponseDto checkPrevGame(String token);

    // 진행 중인 게임 삭제
    DeletePrevGameResponseDto deletePrevGame(String token);

    // 진행 중인 게임 이어하기
    GameStartResponseDto resumePrevGame(String token);

    // 새로운 게임 시작하기
    GameStartResponseDto startNewGame(CreateRoomRequestServiceDto createRoomRequestServiceDto);

}
