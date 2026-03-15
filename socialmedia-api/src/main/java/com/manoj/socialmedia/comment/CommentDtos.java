package com.manoj.socialmedia.comment;

import com.manoj.socialmedia.user.UserDtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class CommentDtos {

    @Data
    public static class CreateCommentRequest {
        @NotBlank
        @Size(max = 500)
        private String content;
    }

    @Data
    @Builder
    public static class CommentResponse {
        private Long id;
        private String content;
        private UserDtos.UserSummary author;
        private Long postId;
        private LocalDateTime createdAt;
    }
}
