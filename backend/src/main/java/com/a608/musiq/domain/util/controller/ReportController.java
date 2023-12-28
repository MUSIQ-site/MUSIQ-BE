package com.a608.musiq.domain.util.controller;

import com.a608.musiq.domain.util.dto.requestDto.SaveSystemReportRequestDto;
import com.a608.musiq.domain.util.dto.responseDto.SaveSystemReportResponseDto;
import com.a608.musiq.domain.util.service.ReportService;
import com.a608.musiq.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/util")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     *
     * @param accessToken
     * @param saveSystemReportRequestDto
     * @see SaveSystemReportResponseDto
     * @return
     */
    @PostMapping("/report")
    @ResponseBody
    private ResponseEntity<BaseResponse<SaveSystemReportResponseDto>> saveSystemReport(
            @RequestHeader("accessToken") String accessToken,
            @RequestBody SaveSystemReportRequestDto saveSystemReportRequestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.<SaveSystemReportResponseDto>builder()
                        .data(reportService.systemReporting(accessToken, saveSystemReportRequestDto))
                        .build());
    }
}
