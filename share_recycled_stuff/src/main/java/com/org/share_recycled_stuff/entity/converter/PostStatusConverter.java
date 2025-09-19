package com.org.share_recycled_stuff.entity.converter;

import com.org.share_recycled_stuff.entity.enums.PostStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PostStatusConverter implements AttributeConverter<PostStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PostStatus status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public PostStatus convertToEntityAttribute(Integer code) {
        return code != null ? PostStatus.fromCode(code) : null;
    }
}
