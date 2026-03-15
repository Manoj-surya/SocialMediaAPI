package com.manoj.socialmedia.user;

import com.manoj.socialmedia.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserDtos.UserProfile>> getProfile(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails currentUser) {
        String currentUsername = currentUser != null ? currentUser.getUsername() : null;
        return ResponseEntity.ok(ApiResponse.success(
                userService.getProfile(username, currentUsername)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDtos.UserProfile>> updateProfile(
            @Valid @RequestBody UserDtos.UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                "Profile updated successfully.",
                userService.updateProfile(currentUser.getUsername(), request)));
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<ApiResponse<Void>> follow(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails currentUser) {
        userService.followUser(currentUser.getUsername(), username);
        return ResponseEntity.ok(ApiResponse.success("Now following " + username + ".", null));
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails currentUser) {
        userService.unfollowUser(currentUser.getUsername(), username);
        return ResponseEntity.ok(ApiResponse.success("Unfollowed " + username + ".", null));
    }
}
