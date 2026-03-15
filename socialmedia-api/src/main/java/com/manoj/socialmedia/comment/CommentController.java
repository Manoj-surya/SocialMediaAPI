package com.manoj.socialmedia.comment;

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
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentDtos.CommentResponse>> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentDtos.CreateCommentRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        CommentDtos.CommentResponse comment =
                commentService.addComment(postId, currentUser.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment added.", comment));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CommentDtos.CommentResponse>>> getComments(
            @PathVariable Long postId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                commentService.getComments(postId, pageable)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        commentService.deleteComment(commentId, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Comment deleted.", null));
    }
}
