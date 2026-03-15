package com.manoj.socialmedia.comment;

import com.manoj.socialmedia.exception.ResourceNotFoundException;
import com.manoj.socialmedia.exception.UnauthorizedException;
import com.manoj.socialmedia.notification.NotificationService;
import com.manoj.socialmedia.post.Post;
import com.manoj.socialmedia.post.PostService;
import com.manoj.socialmedia.user.User;
import com.manoj.socialmedia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public CommentDtos.CommentResponse addComment(Long postId, String username,
                                                   CommentDtos.CreateCommentRequest request) {
        Post post = postService.findById(postId);
        User author = userService.findByUsername(username);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .author(author)
                .post(post)
                .build();

        Comment saved = commentRepository.save(comment);
        notificationService.sendCommentNotification(author, post, saved.getId());

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<CommentDtos.CommentResponse> getComments(Long postId, Pageable pageable) {
        postService.findById(postId); // validate post exists
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not allowed to delete this comment.");
        }

        commentRepository.delete(comment);
    }

    private CommentDtos.CommentResponse mapToResponse(Comment comment) {
        return CommentDtos.CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(userService.mapToSummary(comment.getAuthor()))
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
