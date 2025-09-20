package com.org.share_recycled_stuff.entity.converter;

import com.org.share_recycled_stuff.entity.enums.DelegationRequestsStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DelegationRequestsStatusConverter implements AttributeConverter<DelegationRequestsStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(DelegationRequestsStatus status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public DelegationRequestsStatus convertToEntityAttribute(Integer code) {
        return code != null ? DelegationRequestsStatus.fromCode(code) : null;
    }
}
