package com.a608.musiq.domain.util.domain;

import com.a608.musiq.domain.util.dto.requestDto.ReportType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

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
