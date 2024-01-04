package com.a608.musiq.domain.websocket.domain.log;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiModeModifyGameRoomInformationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column
    private int multiModeCreateGameRoomLogId;

    @NotNull
    @Column
    private String previousTitle;

    @NotNull
    @Column
    private String previousYear;

    @NotNull
    @Column
    private int previousQuizAmount;

    @NotNull
    @Column
    private int previousMaxUserNumber;

    @NotNull
    @Column
    private String modifiedTitle;

    @NotNull
    @Column
    private String modifiedYear;

    @NotNull
    @Column
    private int modifiedQuizAMount;

    @NotNull
    @Column
    private int modifiedMaxUserNumber;
}