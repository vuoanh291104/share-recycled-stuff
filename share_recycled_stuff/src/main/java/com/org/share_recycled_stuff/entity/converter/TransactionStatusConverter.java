package com.org.share_recycled_stuff.entity.converter;

import com.org.share_recycled_stuff.entity.enums.TransactionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TransactionStatusConverter implements AttributeConverter<TransactionStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TransactionStatus status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public TransactionStatus convertToEntityAttribute(Integer code) {
        return code != null ? TransactionStatus.fromCode(code) : null;
    }
}
