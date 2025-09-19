package com.org.share_recycled_stuff.entity.converter;

import com.org.share_recycled_stuff.entity.enums.ReportStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReportStatusConverter implements AttributeConverter<ReportStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ReportStatus status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public ReportStatus convertToEntityAttribute(Integer code) {
        return code != null ? ReportStatus.fromCode(code) : null;
    }
}
