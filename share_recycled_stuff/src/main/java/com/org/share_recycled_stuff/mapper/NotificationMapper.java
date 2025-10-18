package com.org.share_recycled_stuff.mapper;

import com.org.share_recycled_stuff.dto.response.NotificationResponse;
import com.org.share_recycled_stuff.entity.Notifications;
import com.org.share_recycled_stuff.entity.enums.NotificationType;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    @Mapping(target = "notificationTypeName", source = "notificationType", qualifiedByName = "mapTypeName")
    NotificationResponse toResponse(Notifications notification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", source = "account")
    @Mapping(target = "deliveryMethod", expression = "java(deliveryMethod != null ? deliveryMethod : 1)")
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Notifications toEntity(String title, String content, Integer notificationType,
                           Integer deliveryMethod, String relatedEntityType,
                           Long relatedEntityId, com.org.share_recycled_stuff.entity.Account account);

    @AfterMapping
    default void setIsRead(@MappingTarget NotificationResponse response, Notifications notification) {
        response.setRead(notification.isRead());
    }

    @Named("mapTypeName")
    default String mapTypeName(Integer notificationType) {
        if (notificationType == null) {
            return null;
        }
        try {
            return NotificationType.fromCode(notificationType).getDisplayName();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

