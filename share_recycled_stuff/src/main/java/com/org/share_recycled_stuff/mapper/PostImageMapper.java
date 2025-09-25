package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.request.CreatePostImageRequest;
import com.org.share_recycled_stuff.dto.response.PostImageResponse;
import com.org.share_recycled_stuff.entity.PostImages;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostImageMapper {
    PostImages toEntity (CreatePostImageRequest createPostImageRequest);
    PostImageResponse toResponse (PostImages postImages);
}
