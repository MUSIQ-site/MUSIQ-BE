package com.a608.musiq.domain.admin.dto.responseDto;

import com.a608.musiq.domain.admin.dto.GetReportBugItem;
import com.a608.musiq.domain.admin.dto.GetReportSuggestionItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetReportResponseDto {
    private List<GetReportBugItem> bugItems;
    private List<GetReportSuggestionItem> suggestionItems;

    public static GetReportResponseDto from(List<GetReportBugItem> bugItems, List<GetReportSuggestionItem> suggestionItems) {
        return GetReportResponseDto.builder()
                .bugItems(bugItems)
                .suggestionItems(suggestionItems)
                .build();
    }
}
