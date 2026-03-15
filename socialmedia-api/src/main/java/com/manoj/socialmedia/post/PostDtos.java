package com.manoj.socialmedia.post;

import com.manoj.socialmedia.user.UserDtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class PostDtos {

    @Data
    public static class CreatePostRequest {
        @NotBlank
        @Size(max = 1000)
        private String content;

        private String imageUrl;
    }

    @Data
    @Builder
    public static class PostResponse {
        private Long id;
        private String content;
        private String imageUrl;
        private UserDtos.UserSummary author;
        private int likesCount;
        private int commentsCount;
        private boolean likedByCurrentUser;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
