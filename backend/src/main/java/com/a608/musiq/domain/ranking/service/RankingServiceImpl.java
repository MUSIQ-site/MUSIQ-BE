package com.a608.musiq.domain.ranking.service;

import com.a608.musiq.domain.ranking.dto.responseDto.FullRankResponseDto;
import com.a608.musiq.domain.ranking.dto.responseDto.MyRankResponseDto;
import com.a608.musiq.domain.ranking.dto.view.FullRankItem;

import com.a608.musiq.global.Util.RedisKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.a608.musiq.global.Util;
import com.a608.musiq.global.exception.exception.RankingException;
import com.a608.musiq.global.exception.info.RankingExceptionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

    private final Util util;

    /**
     * 순위를 찾는 메서드
     * @param nickname
     * @return String - 순위 값 또는 순위권 밖
     */
    private String findMyRank(String nickname) {

        Double myScore = util.getScorefromSortedSet(RedisKey.RANKING.getKey(), nickname);
        String rankNum = "순위권 외";
        if(myScore != null) {
            Long count = util.countInSortedSet(RedisKey.RANKING.getKey(), myScore+0.001, Double.MAX_VALUE);
            rankNum = String.valueOf(count < 100 ? count+1 : "순위권 외");
        }
        return rankNum;
    }

    /**
     * 순위를 가져와 Dto에 담는 메서드
     * @param nickname - 조회하려는 사용자의 닉네임
     * @return MyRankResponseDto - 조회된 사용자의 랭킹
     */
    @Override
    public MyRankResponseDto RankNumToDto(String nickname) {

        return MyRankResponseDto.builder().rankNum(findMyRank(nickname)).build();
    }

    /**
     * 전체 랭킹을 조회하는 메서드
     *
     * @param nickname
     * @param size
     * @return FullRankResponseDto -
     */
    @Override
    public FullRankResponseDto getFullRank(String nickname, Integer size) {

        // util의 메서드를 사용하여 전체 랭킹 가져오기
        Set<TypedTuple<String>> rankingSet = util.getRangeFromRedisSortedSet(RedisKey.RANKING.getKey(), 0, size);

        // ranking set이 비어있다면 예외 처리
        if (rankingSet.isEmpty()) {
            return FullRankResponseDto.builder()
                    .rankList(new ArrayList<>())
                    .myRank(null)
                    .build();
        }

        // nickname null 여부 확인
        if (nickname == null)
            // 닉네임이 없다면 로그인이 되어 있지 않은 것
            throw new RankingException(RankingExceptionInfo.NOT_LOGIN);

        String myRank = findMyRank(nickname);

        // 결과를 리스트로 옮기기
        List<FullRankItem> fullRankList = rankingSet.stream().map(setElem -> new FullRankItem(setElem)).collect(Collectors.toList());

        // Set은 순서를 보장하지 않기 때문에 정렬
        Collections.sort(fullRankList, (o1, o2) -> {
            return Double.compare(o2.getExp(), o1.getExp());
        });

        // 랭킹 번호 처리 (공동 등수 처리)
        int combo = 1;
        fullRankList.get(0).setRankNum(1);
        for(int i=1; i<fullRankList.size(); i++) {

            FullRankItem prev = fullRankList.get(i-1);
            FullRankItem now = fullRankList.get(i);

            // 경험치가 다른 경우 (공동 등수가 아닌 경우)
            if(!prev.getExp().equals(now.getExp())) {
                now.setRankNum(prev.getRankNum() + combo);
                combo = 1;
            }
            // 경험치가 같은 경우 (공동 등수인 경우)
            else {
                now.setRankNum(prev.getRankNum());
                combo++;
            }
        }

        return FullRankResponseDto.builder()
                .rankList(fullRankList)
                .myRank(myRank)
                .build();
    }

    @Override
    public void testAdd(String nickname, double exp) throws Exception {
        util.insertDatatoRedisSortedSet(RedisKey.RANKING.getKey(), nickname, exp);
    }

    @Override
    public void testClear() throws Exception {
        util.deleteKeyInRedis(RedisKey.RANKING.getKey());
    }

}
