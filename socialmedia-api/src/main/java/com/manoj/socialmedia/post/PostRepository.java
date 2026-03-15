package com.manoj.socialmedia.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    // Feed: posts from users that the current user follows
    @Query("""
        SELECT p FROM Post p
        WHERE p.author.id IN (
            SELECT u.id FROM User u JOIN u.followers f WHERE f.id = :userId
        )
        ORDER BY p.createdAt DESC
    """)
    Page<Post> findFeedForUser(@Param("userId") Long userId, Pageable pageable);
}
