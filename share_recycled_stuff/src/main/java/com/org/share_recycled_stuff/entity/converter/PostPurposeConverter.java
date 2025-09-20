package com.org.share_recycled_stuff.entity.converter;

import com.org.share_recycled_stuff.entity.enums.PostPurpose;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PostPurposeConverter implements AttributeConverter<PostPurpose, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PostPurpose purpose) {
        return purpose != null ? purpose.getCode() : null;
    }

    @Override
    public PostPurpose convertToEntityAttribute(Integer code) {
        return code != null ? PostPurpose.fromCode(code) : null;
    }
}
