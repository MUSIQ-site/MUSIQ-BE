package com.a608.musiq.domain.admin.controller;

import com.a608.musiq.domain.admin.dto.responseDto.GetBugReportResponseDto;
import com.a608.musiq.domain.admin.dto.responseDto.GetReportResponseDto;
import com.a608.musiq.domain.admin.dto.responseDto.GetSuggestionResponseDto;
import com.a608.musiq.domain.util.service.ReportService;
import com.a608.musiq.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/no-access-anybody")
@RequiredArgsConstructor
public class AdminController {
    private final ReportService reportService;

    /**
     * 신고 전체 조회
     *
     * @param page
     * @param size
     */
    @GetMapping("/report")
    private ResponseEntity<BaseResponse<GetReportResponseDto>> getReport(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(BaseResponse.<GetReportResponseDto>builder()
                .code(HttpStatus.OK.value())
                .data(reportService.getReport(page, size))
                .build());
    }

    /**
     * 건의 조회
     *
     * @param page
     * @param size
     */
    @GetMapping("/suggestion")
    private ResponseEntity<BaseResponse<GetSuggestionResponseDto>> getSuggestionReport(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(BaseResponse.<GetSuggestionResponseDto>builder()
                .code(HttpStatus.OK.value())
                .data(reportService.getSuggestionReport(page, size))
                .build());
    }

    /**
     * 버그 신고 조회
     *
     * @param page
     * @param size
     */
    @GetMapping("/bug")
    private ResponseEntity<BaseResponse<GetBugReportResponseDto>> getBugReport(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(BaseResponse.<GetBugReportResponseDto>builder()
                .code(HttpStatus.OK.value())
                .data(reportService.getBugReport(page, size))
                .build());
    }

}
