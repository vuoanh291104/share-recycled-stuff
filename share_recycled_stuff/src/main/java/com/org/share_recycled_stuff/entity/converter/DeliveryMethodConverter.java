package com.org.share_recycled_stuff.entity.converter;

import com.org.share_recycled_stuff.entity.enums.DeliveryMethod;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DeliveryMethodConverter implements AttributeConverter<DeliveryMethod, Integer> {

    @Override
    public Integer convertToDatabaseColumn(DeliveryMethod method) {
        return method != null ? method.getCode() : null;
    }

    @Override
    public DeliveryMethod convertToEntityAttribute(Integer code) {
        return code != null ? DeliveryMethod.fromCode(code) : null;
    }
}
