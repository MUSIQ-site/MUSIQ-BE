package com.a608.musiq.global.scheduler;

import com.a608.musiq.domain.member.domain.MemberInfo;
import com.a608.musiq.domain.member.repository.MemberInfoRepository;
import com.a608.musiq.domain.websocket.service.GameService;
import com.a608.musiq.global.Util;
import com.a608.musiq.global.Util.RedisKey;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final Util util;
    
    private final MemberInfoRepository memberInfoRepository;
    private final GameService gameService;


    // 매일 새벽 4시 0분 0초마다 실행됨
    @Scheduled(cron = "0 0 4 * * *")
    private void deleteRanking(){

        util.deleteKeyInRedis("ranking");
    }

    // 매일 새벽 4시 0분 1초마다 실행됨
    @Scheduled(cron = "1 0 4 * * *")
    private void insertRanking(){

        insertRankingToRedis();
    }

    //1초마다 모든 멀티방 -1
    @Scheduled(cron = "*/1 * * * * *")
    private void multiModeCountDown(){
        gameService.pubMessage();
    }

    public void insertRankingToRedis() {
        int num = 100;
        Pageable pageRequest = PageRequest.of(0, num, Sort.by(Sort.Order.desc("exp")));
        List<MemberInfo> rankingList = memberInfoRepository.findByDeleted(false, pageRequest);

        for(MemberInfo memberInfo : rankingList) {
            util.insertDatatoRedisSortedSet(RedisKey.RANKING.getKey(), memberInfo.getNickname(), memberInfo.getExp());
        }
    }

}
