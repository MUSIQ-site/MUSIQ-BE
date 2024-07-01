package com.a608.musiq.domain.admin.dto.responseDto;

import com.a608.musiq.domain.admin.dto.GetReportItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetReportResponseDto {
    private long totalAmount;
    private List<GetReportItem> reportItems;

    public static GetReportResponseDto from(long totalAmount, List<GetReportItem> reportItems) {
        return GetReportResponseDto.builder()
                .totalAmount(totalAmount)
                .reportItems(reportItems)
                .build();
    }
}
