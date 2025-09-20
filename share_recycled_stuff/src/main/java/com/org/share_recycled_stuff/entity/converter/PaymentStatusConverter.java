package com.org.share_recycled_stuff.entity.converter;

import com.org.share_recycled_stuff.entity.enums.PaymentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PaymentStatus status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public PaymentStatus convertToEntityAttribute(Integer code) {
        return code != null ? PaymentStatus.fromCode(code) : null;
    }
}
