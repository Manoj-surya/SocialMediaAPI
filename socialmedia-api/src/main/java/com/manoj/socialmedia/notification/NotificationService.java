package com.manoj.socialmedia.notification;

import com.manoj.socialmedia.post.Post;
import com.manoj.socialmedia.user.User;
import com.manoj.socialmedia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    public void sendLikeNotification(User actor, Post post) {
        // Don't notify if user likes their own post
        if (actor.getId().equals(post.getAuthor().getId())) return;

        Notification notification = save(actor, post.getAuthor(),
                Notification.NotificationType.LIKE, post.getId());
        pushToUser(post.getAuthor().getUsername(), mapToResponse(notification));
    }

    public void sendCommentNotification(User actor, Post post, Long commentId) {
        if (actor.getId().equals(post.getAuthor().getId())) return;

        Notification notification = save(actor, post.getAuthor(),
                Notification.NotificationType.COMMENT, commentId);
        pushToUser(post.getAuthor().getUsername(), mapToResponse(notification));
    }

    public void sendFollowNotification(User actor, User target) {
        Notification notification = save(actor, target,
                Notification.NotificationType.FOLLOW, actor.getId());
        pushToUser(target.getUsername(), mapToResponse(notification));
    }

    @Transactional(readOnly = true)
    public Page<NotificationDtos.NotificationResponse> getNotifications(String username, Pageable pageable) {
        User user = userService.findByUsername(username);
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public void markAllRead(String username) {
        User user = userService.findByUsername(username);
        notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId(), Pageable.unpaged())
                .forEach(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String username) {
        User user = userService.findByUsername(username);
        return notificationRepository.countByRecipientIdAndReadFalse(user.getId());
    }

    private Notification save(User actor, User recipient,
                               Notification.NotificationType type, Long referenceId) {
        Notification notification = Notification.builder()
                .actor(actor)
                .recipient(recipient)
                .type(type)
                .referenceId(referenceId)
                .build();
        return notificationRepository.save(notification);
    }

    private void pushToUser(String username, NotificationDtos.NotificationResponse payload) {
        try {
            messagingTemplate.convertAndSendToUser(username, "/queue/notifications", payload);
        } catch (Exception e) {
            log.warn("Failed to push WebSocket notification to {}: {}", username, e.getMessage());
        }
    }

    private NotificationDtos.NotificationResponse mapToResponse(Notification n) {
        return NotificationDtos.NotificationResponse.builder()
                .id(n.getId())
                .actor(userService.mapToSummary(n.getActor()))
                .type(n.getType())
                .referenceId(n.getReferenceId())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
