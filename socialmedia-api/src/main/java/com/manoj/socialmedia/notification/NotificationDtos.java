package com.manoj.socialmedia.notification;

import com.manoj.socialmedia.user.UserDtos;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class NotificationDtos {

    @Data
    @Builder
    public static class NotificationResponse {
        private Long id;
        private UserDtos.UserSummary actor;
        private Notification.NotificationType type;
        private Long referenceId;
        private boolean read;
        private LocalDateTime createdAt;
    }
}
