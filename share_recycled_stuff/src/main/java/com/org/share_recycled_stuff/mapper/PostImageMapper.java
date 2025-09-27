package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.request.PostImageRequest;
import com.org.share_recycled_stuff.dto.response.PostImageResponse;
import com.org.share_recycled_stuff.entity.PostImages;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostImageMapper {
    PostImages toEntity (PostImageRequest postImageRequest);
    PostImageResponse toResponse (PostImages postImages);
    void updateImage (PostImageRequest postImageRequest, @MappingTarget PostImages entity);
}
