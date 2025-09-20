package com.org.share_recycled_stuff.entity.converter;

import com.org.share_recycled_stuff.entity.enums.ReportType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReportTypeConverter implements AttributeConverter<ReportType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ReportType type) {
        return type != null ? type.getCode() : null;
    }

    @Override
    public ReportType convertToEntityAttribute(Integer code) {
        return code != null ? ReportType.fromCode(code) : null;
    }
}
