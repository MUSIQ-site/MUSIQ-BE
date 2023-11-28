package com.a608.musiq.domain.ranking.controller;

import com.a608.musiq.domain.ranking.dto.responseDto.FullRankResponseDto;
import com.a608.musiq.domain.ranking.dto.responseDto.MyRankResponseDto;
import com.a608.musiq.domain.ranking.service.RankingService;
import com.a608.musiq.global.Util;
import com.a608.musiq.global.common.response.BaseResponse;
import com.a608.musiq.global.scheduler.Scheduler;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;
    private final Scheduler scheduler;
    private final Util util;

    @GetMapping("/myranking")
    private ResponseEntity<BaseResponse<MyRankResponseDto>> getMyRanking(@PathParam("nickname") String nickname) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.<MyRankResponseDto>builder()
                        .code(HttpStatus.OK.value())
                        .data(rankingService.RankNumToDto(nickname))
                        .build());
    }

    @GetMapping("/fullranking")
    private ResponseEntity<BaseResponse<FullRankResponseDto>> getFullRanking(@PathParam("nickname") String nickname) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.<FullRankResponseDto>builder()
                        .code(HttpStatus.OK.value())
                        .data(rankingService.getFullRank(nickname, 100))
                        .build());
    }

    @PostMapping("/testadd")
    private BaseResponse<?> testAdd(@PathParam("nickname") String nickname, @PathParam("exp") double exp) {
        try {
            rankingService.testAdd(nickname, exp);
            return BaseResponse.builder().code(HttpStatus.OK.value()).build();
        } catch (Exception e) {
            return BaseResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        }
    }

    @DeleteMapping("/testclear")
    private BaseResponse<?> testClear() {
        try {
            rankingService.testClear();
            return BaseResponse.builder().code(HttpStatus.OK.value()).build();
        } catch (Exception e) {
            return BaseResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        }
    }

    @PostMapping("/into-redis")
    private void insertRanking(){
        scheduler.insertRankingToRedis();
    }

    @DeleteMapping("/init-redis")
    private void deleteRanking(){
        util.deleteKeyInRedis("ranking");
    }

}
