package com.a608.musiq.domain.util.Data;

import com.a608.musiq.global.exception.exception.AdminException;
import com.a608.musiq.global.exception.info.AdminExceptionInfo;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReportType {
    Suggestion("SUGGESTION"),
    Bug("BUG");

    private final String value;

    ReportType(String value) {
        this.value = value;
    }

    public static ReportType ofValue(String value) {
        return Arrays.stream(ReportType.values())
                .filter(type -> type.getValue().equals(value))
                .findAny()
                .orElseThrow(() -> new AdminException(AdminExceptionInfo.INVALID_TYPE));
    }
}
