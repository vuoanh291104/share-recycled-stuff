package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.response.UserDetailResponse;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.UserRole;
import com.org.share_recycled_stuff.entity.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.phone", target = "phoneNumber")
    @Mapping(source = "user.address", target = "address")
    @Mapping(source = "user.avatarUrl", target = "avatarUrl")
    @Mapping(target = "status", expression = "java(mapStatus(account))")
    @Mapping(target = "roles", expression = "java(mapRoles(account))")
    @Mapping(source = "locked", target = "isLocked")
    @Mapping(source = "lockedReason", target = "lockReason")
    @Mapping(source = "lockedAt", target = "lockedAt")
    @Mapping(target = "lockedBy", expression = "java(mapLockedBy(account))")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    UserDetailResponse toUserDetailResponse(Account account);

    default String mapStatus(Account account) {
        return account.isLocked() ? "LOCKED" : "ACTIVE";
    }

    default Set<Role> mapRoles(Account account) {
        if (account == null || account.getRoles() == null) {
            return null;
        }
        return account.getRoles().stream()
                .map(UserRole::getRoleType)
                .collect(Collectors.toSet());
    }

    default String mapLockedBy(Account account) {
        if (account == null || !account.isLocked()) {
            return null;
        }
        return "SYSTEM";
    }
}
