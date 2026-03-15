package com.manoj.socialmedia.post;

import com.manoj.socialmedia.exception.ResourceNotFoundException;
import com.manoj.socialmedia.exception.UnauthorizedException;
import com.manoj.socialmedia.notification.NotificationService;
import com.manoj.socialmedia.user.User;
import com.manoj.socialmedia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public PostDtos.PostResponse createPost(String username, PostDtos.CreatePostRequest request) {
        User author = userService.findByUsername(username);

        Post post = Post.builder()
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .author(author)
                .build();

        return mapToResponse(postRepository.save(post), author);
    }

    @Transactional(readOnly = true)
    public Page<PostDtos.PostResponse> getFeed(String username, Pageable pageable) {
        User user = userService.findByUsername(username);
        return postRepository.findFeedForUser(user.getId(), pageable)
                .map(post -> mapToResponse(post, user));
    }

    @Transactional(readOnly = true)
    public Page<PostDtos.PostResponse> getUserPosts(String username, String currentUsername, Pageable pageable) {
        User author = userService.findByUsername(username);
        User currentUser = userService.findByUsername(currentUsername);
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(author.getId(), pageable)
                .map(post -> mapToResponse(post, currentUser));
    }

    @Transactional(readOnly = true)
    public PostDtos.PostResponse getPost(Long postId, String currentUsername) {
        Post post = findById(postId);
        User currentUser = userService.findByUsername(currentUsername);
        return mapToResponse(post, currentUser);
    }

    @Transactional
    public void deletePost(Long postId, String username) {
        Post post = findById(postId);
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not allowed to delete this post.");
        }
        postRepository.delete(post);
    }

    @Transactional
    public PostDtos.PostResponse likePost(Long postId, String username) {
        Post post = findById(postId);
        User user = userService.findByUsername(username);

        if (!post.getLikes().contains(user)) {
            post.getLikes().add(user);
            postRepository.save(post);
            notificationService.sendLikeNotification(user, post);
        }

        return mapToResponse(post, user);
    }

    @Transactional
    public PostDtos.PostResponse unlikePost(Long postId, String username) {
        Post post = findById(postId);
        User user = userService.findByUsername(username);
        post.getLikes().remove(user);
        return mapToResponse(postRepository.save(post), user);
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    private PostDtos.PostResponse mapToResponse(Post post, User currentUser) {
        return PostDtos.PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .author(userService.mapToSummary(post.getAuthor()))
                .likesCount(post.getLikes().size())
                .commentsCount(post.getComments().size())
                .likedByCurrentUser(post.getLikes().contains(currentUser))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
