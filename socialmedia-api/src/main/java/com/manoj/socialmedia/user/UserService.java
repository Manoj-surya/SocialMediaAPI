package com.manoj.socialmedia.user;

import com.manoj.socialmedia.exception.BadRequestException;
import com.manoj.socialmedia.exception.ResourceNotFoundException;
import com.manoj.socialmedia.notification.NotificationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public UserService(UserRepository userRepository,
                       @Lazy NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public UserDtos.UserProfile getProfile(String username, String currentUsername) {
        User user = findByUsername(username);
        User currentUser = currentUsername != null
                ? findByUsername(currentUsername)
                : null;

        boolean isFollowing = currentUser != null &&
                user.getFollowers().contains(currentUser);

        return mapToProfile(user, isFollowing);
    }

    @Transactional
    public UserDtos.UserProfile updateProfile(String username, UserDtos.UpdateProfileRequest request) {
        User user = findByUsername(username);

        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getProfilePictureUrl() != null) user.setProfilePictureUrl(request.getProfilePictureUrl());

        userRepository.save(user);
        return mapToProfile(user, false);
    }

    @Transactional
    public void followUser(String currentUsername, String targetUsername) {
        if (currentUsername.equals(targetUsername)) {
            throw new BadRequestException("You cannot follow yourself.");
        }

        User currentUser = findByUsername(currentUsername);
        User targetUser = findByUsername(targetUsername);

        if (targetUser.getFollowers().contains(currentUser)) {
            throw new BadRequestException("You are already following this user.");
        }

        targetUser.getFollowers().add(currentUser);
        userRepository.save(targetUser);

        notificationService.sendFollowNotification(currentUser, targetUser);
    }

    @Transactional
    public void unfollowUser(String currentUsername, String targetUsername) {
        User currentUser = findByUsername(currentUsername);
        User targetUser = findByUsername(targetUsername);

        if (!targetUser.getFollowers().contains(currentUser)) {
            throw new BadRequestException("You are not following this user.");
        }

        targetUser.getFollowers().remove(currentUser);
        userRepository.save(targetUser);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    public UserDtos.UserSummary mapToSummary(User user) {
        return UserDtos.UserSummary.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }

    private UserDtos.UserProfile mapToProfile(User user, boolean isFollowing) {
        return UserDtos.UserProfile.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .followersCount(user.getFollowers().size())
                .followingCount(user.getFollowing().size())
                .isFollowing(isFollowing)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
