package com.org.share_recycled_stuff.entity.converter;

import com.org.share_recycled_stuff.entity.enums.PaymentMethod;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PaymentMethod method) {
        return method != null ? method.getCode() : null;
    }

    @Override
    public PaymentMethod convertToEntityAttribute(Integer code) {
        return code != null ? PaymentMethod.fromCode(code) : null;
    }
}
