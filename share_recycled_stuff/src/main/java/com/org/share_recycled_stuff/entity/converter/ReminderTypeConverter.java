package com.org.share_recycled_stuff.entity.converter;

import com.org.share_recycled_stuff.entity.enums.ReminderType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReminderTypeConverter implements AttributeConverter<ReminderType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ReminderType type) {
        return type != null ? type.getCode() : null;
    }

    @Override
    public ReminderType convertToEntityAttribute(Integer code) {
        return code != null ? ReminderType.fromCode(code) : null;
    }
}
