package com.a608.musiq.domain.admin.dto.responseDto;

import com.a608.musiq.domain.admin.dto.GetReportItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetReportResponseDto {
    private long totalAmount;
    private int page;
    private int size;
    private List<GetReportItem> reportItems;

    public static GetReportResponseDto from(long totalAmount, int page, int size, List<GetReportItem> reportItems) {
        return GetReportResponseDto.builder()
                .totalAmount(totalAmount)
                .page(page)
                .size(size)
                .reportItems(reportItems)
                .build();
    }
}
