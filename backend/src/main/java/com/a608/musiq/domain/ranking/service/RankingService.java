package com.a608.musiq.domain.ranking.service;

import com.a608.musiq.domain.ranking.dto.responseDto.FullRankResponseDto;
import com.a608.musiq.domain.ranking.dto.responseDto.MyRankResponseDto;

public interface RankingService {
    MyRankResponseDto RankNumToDto(String nickname);
    FullRankResponseDto getFullRank(String nickname, Integer size);
    void testAdd(String nickname, double exp) throws Exception;
    void testClear() throws Exception;
}
