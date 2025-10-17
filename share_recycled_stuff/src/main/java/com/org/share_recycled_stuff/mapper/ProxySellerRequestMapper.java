package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse;
import com.org.share_recycled_stuff.entity.ProxySellerRequests;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProxySellerRequestMapper {

    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "fullName", source = "account.user.fullName")
    @Mapping(target = "email", source = "account.email")
    UpgradeRequestResponse toResponse(ProxySellerRequests request);
}

