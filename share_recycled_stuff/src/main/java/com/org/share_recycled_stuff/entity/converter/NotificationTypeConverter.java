package com.org.share_recycled_stuff.entity.converter;

import com.org.share_recycled_stuff.entity.enums.NotificationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NotificationTypeConverter implements AttributeConverter<NotificationType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(NotificationType type) {
        return type != null ? type.getCode() : null;
    }

    @Override
    public NotificationType convertToEntityAttribute(Integer code) {
        return code != null ? NotificationType.fromCode(code) : null;
    }
}
