package com.a608.musiq.domain.admin.dto.responseDto;

import com.a608.musiq.domain.admin.dto.GetBugReportItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetBugReportResponseDto {
    private long bugReportAmount;
    private int page;
    private int size;
    private List<GetBugReportItem> bugReportItems;

    public static GetBugReportResponseDto from (long bugReportAmount, int page, int size, List<GetBugReportItem> bugReportItems) {
        return GetBugReportResponseDto.builder()
            .bugReportAmount(bugReportAmount)
            .page(page)
            .size(size)
            .bugReportItems(bugReportItems)
            .build();
    }
}
