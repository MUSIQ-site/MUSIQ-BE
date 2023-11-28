package com.a608.musiq.domain.websocket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoItem {
    private static final Double INITIAL_SCORE = 0.0;
    private static final Boolean INITIAL_IS_SKIPPED = Boolean.FALSE;

    private String nickname;
    private Double score;
    private Boolean isSkipped;


    public static UserInfoItem create(String nickname, Double score, Boolean isSkipped) {
        return new UserInfoItem(nickname, score, isSkipped);
    }

    public static UserInfoItem of(String nickname) {
        return UserInfoItem.builder()
            .nickname(nickname)
            .score(INITIAL_SCORE)
            .isSkipped(INITIAL_IS_SKIPPED)
            .build();
    }

    public void setSkipped(Boolean skipped) {
        isSkipped = skipped;
    }


    public void initializeUserInfo() {
        this.score = INITIAL_SCORE;
        this.isSkipped = INITIAL_IS_SKIPPED;
    }

    public void setScore(Double score) {
        this.score = score;
    }
    public void upScore(){
        this.score++;

    }
}
