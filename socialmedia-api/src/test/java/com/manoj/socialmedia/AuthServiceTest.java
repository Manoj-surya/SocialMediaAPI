package com.manoj.socialmedia;

import com.manoj.socialmedia.auth.AuthDtos;
import com.manoj.socialmedia.auth.AuthService;
import com.manoj.socialmedia.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private AuthDtos.RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new AuthDtos.RegisterRequest();
        validRequest.setUsername("testuser");
        validRequest.setEmail("test@example.com");
        validRequest.setPassword("password123");
        validRequest.setDisplayName("Test User");
    }

    @Test
    @DisplayName("Should register a new user and return a JWT token")
    void register_ShouldCreateUserAndReturnToken() {
        AuthDtos.AuthResponse response = authService.register(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(userRepository.existsByUsername("testuser")).isTrue();
    }

    @Test
    @DisplayName("Should throw BadRequestException when username is already taken")
    void register_ShouldThrowWhenUsernameTaken() {
        authService.register(validRequest);

        AuthDtos.RegisterRequest duplicate = new AuthDtos.RegisterRequest();
        duplicate.setUsername("testuser");
        duplicate.setEmail("other@example.com");
        duplicate.setPassword("password123");

        assertThatThrownBy(() -> authService.register(duplicate))
                .isInstanceOf(com.manoj.socialmedia.exception.BadRequestException.class)
                .hasMessageContaining("already taken");
    }

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void login_ShouldReturnTokenForValidCredentials() {
        authService.register(validRequest);

        AuthDtos.LoginRequest loginRequest = new AuthDtos.LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        AuthDtos.AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should throw exception for wrong password")
    void login_ShouldThrowForWrongPassword() {
        authService.register(validRequest);

        AuthDtos.LoginRequest loginRequest = new AuthDtos.LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    }
}
