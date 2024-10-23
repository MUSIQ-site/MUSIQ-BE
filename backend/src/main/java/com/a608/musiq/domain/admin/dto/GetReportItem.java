package com.a608.musiq.domain.admin.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetReportItem {
    private String nickname;
    private LocalDateTime date;
    private String content;

    public GetReportItem(String nickname, LocalDateTime date, String content) {
        this.nickname = nickname;
        this.date = date;
        this.content = content;
    }
}
