package com.a608.musiq.domain.util.Data;

import jakarta.persistence.AttributeConverter;

public class ReportTypeConverter implements AttributeConverter<ReportType, String> {

    @Override
    public String convertToDatabaseColumn(ReportType reportType) {
        return reportType.getValue();
    }

    @Override
    public ReportType convertToEntityAttribute(String data) {
        return ReportType.ofValue(data);
    }
}
