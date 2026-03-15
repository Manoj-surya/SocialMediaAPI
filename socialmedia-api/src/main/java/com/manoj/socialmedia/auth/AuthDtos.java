package com.manoj.socialmedia.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

public class AuthDtos {

    @Data
    public static class RegisterRequest {
        @NotBlank
        @Size(min = 3, max = 50)
        private String username;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 6, max = 100)
        private String password;

        private String displayName;
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username; // username or email

        @NotBlank
        private String password;
    }

    @Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class AuthResponse {
        private String token;
        @Builder.Default
        private final String tokenType = "Bearer";
        private Long userId;
        private String username;
        private String email;
    }
}
