package com.a608.musiq.global.exception.exception;

import com.a608.musiq.global.exception.info.RankingExceptionInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RankingException extends RuntimeException {
    private final RankingExceptionInfo info;
}
