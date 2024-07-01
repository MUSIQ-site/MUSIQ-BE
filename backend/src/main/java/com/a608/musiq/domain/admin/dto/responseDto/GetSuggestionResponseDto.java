package com.a608.musiq.domain.admin.dto.responseDto;

import com.a608.musiq.domain.admin.dto.GetSuggestionReportItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetSuggestionResponseDto {
    private long suggestionReportAmount;
    private List<GetSuggestionReportItem> suggestionItems;

    public static GetSuggestionResponseDto from(long suggestionReportAmount, List<GetSuggestionReportItem> suggestionItems) {
        return GetSuggestionResponseDto.builder()
                .suggestionReportAmount(suggestionReportAmount)
                .suggestionItems(suggestionItems)
                .build();
    }
}
