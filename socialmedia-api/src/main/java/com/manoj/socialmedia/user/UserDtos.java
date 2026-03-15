package com.manoj.socialmedia.user;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class UserDtos {

    @Data
    @Builder
    public static class UserSummary {
        private Long id;
        private String username;
        private String displayName;
        private String profilePictureUrl;
    }

    @Data
    @Builder
    public static class UserProfile {
        private Long id;
        private String username;
        private String email;
        private String displayName;
        private String bio;
        private String profilePictureUrl;
        private long followersCount;
        private long followingCount;
        private boolean isFollowing;
        private LocalDateTime createdAt;
    }

    @Data
    public static class UpdateProfileRequest {
        @Size(max = 100)
        private String displayName;

        @Size(max = 250)
        private String bio;

        private String profilePictureUrl;
    }
}
