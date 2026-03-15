package com.manoj.socialmedia.post;

import com.manoj.socialmedia.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostDtos.PostResponse>> createPost(
            @Valid @RequestBody PostDtos.CreatePostRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        PostDtos.PostResponse post = postService.createPost(currentUser.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Post created.", post));
    }

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<Page<PostDtos.PostResponse>>> getFeed(
            @AuthenticationPrincipal UserDetails currentUser,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.getFeed(currentUser.getUsername(), pageable)));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<Page<PostDtos.PostResponse>>> getUserPosts(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails currentUser,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.getUserPosts(username, currentUser.getUsername(), pageable)));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDtos.PostResponse>> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.getPost(postId, currentUser.getUsername())));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser) {
        postService.deletePost(postId, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Post deleted.", null));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<PostDtos.PostResponse>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.likePost(postId, currentUser.getUsername())));
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<PostDtos.PostResponse>> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.unlikePost(postId, currentUser.getUsername())));
    }
}
