package com.a608.musiq.domain.admin.dto.responseDto;

import com.a608.musiq.domain.admin.dto.GetSuggestionReportItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetSuggestionResponseDto {
    private long suggestionReportAmount;
    private int page;
    private int size;
    private List<GetSuggestionReportItem> suggestionItems;

    public static GetSuggestionResponseDto from(long suggestionReportAmount, int page, int size, List<GetSuggestionReportItem> suggestionItems) {
        return GetSuggestionResponseDto.builder()
                .suggestionReportAmount(suggestionReportAmount)
                .page(page)
                .size(size)
                .suggestionItems(suggestionItems)
                .build();
    }
}
