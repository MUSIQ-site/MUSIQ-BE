package com.a608.musiq.domain.util.domain;

import com.a608.musiq.domain.util.Data.ReportType;
import com.a608.musiq.domain.util.Data.ReportTypeConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemReportLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column
    @Convert(converter = ReportTypeConverter.class)
    private ReportType type;

    @NotNull
    @Column
    private String loginId;

    @NotNull
    @Column
    private String nickname;

    @NotNull
    @Column
    private String content;

    @NotNull
    @Column
    private LocalDateTime createdAt;
}
