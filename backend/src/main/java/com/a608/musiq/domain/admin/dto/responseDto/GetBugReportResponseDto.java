package com.a608.musiq.domain.admin.dto.responseDto;

import com.a608.musiq.domain.admin.dto.GetBugReportItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetBugReportResponseDto {
    long bugReportAmount;
    List<GetBugReportItem> bugReportItems;

    public static GetBugReportResponseDto from (long bugReportAmount, List<GetBugReportItem> bugReportItems) {
        return GetBugReportResponseDto.builder()
            .bugReportAmount(bugReportAmount)
            .bugReportItems(bugReportItems)
            .build();
    }
}
