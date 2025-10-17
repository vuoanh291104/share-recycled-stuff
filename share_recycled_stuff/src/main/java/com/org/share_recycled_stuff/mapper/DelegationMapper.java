package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.response.DelegationResponse;
import com.org.share_recycled_stuff.entity.DelegationImages;
import com.org.share_recycled_stuff.entity.DelegationRequests;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DelegationMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "proxySellerId", source = "proxySeller.id")
    @Mapping(target = "status", expression = "java(request.getStatus().name())")
    @Mapping(target = "imageUrls", source = "images", qualifiedByName = "mapImageUrls")
    DelegationResponse toResponse(DelegationRequests request);

    @Named("mapImageUrls")
    default Set<String> mapImageUrls(Set<DelegationImages> images) {
        if (images == null) {
            return Collections.emptySet();
        }
        return images.stream()
                .map(DelegationImages::getImageUrl)
                .collect(Collectors.toSet());
    }
}

