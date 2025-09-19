package com.org.share_recycled_stuff.entity.converter;

import com.org.share_recycled_stuff.entity.enums.RequestStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RequestStatusConverter implements AttributeConverter<RequestStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(RequestStatus status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public RequestStatus convertToEntityAttribute(Integer code) {
        return code != null ? RequestStatus.fromCode(code) : null;
    }
}
