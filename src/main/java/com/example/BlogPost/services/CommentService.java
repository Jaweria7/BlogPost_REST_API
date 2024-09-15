package com.example.BlogPost.services;

import com.example.BlogPost.entities.Comment;
import com.example.BlogPost.entities.Post;
import com.example.BlogPost.repositories.CommentRepository;
import com.example.BlogPost.repositories.PostRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment saveOrUpdateComment(Long postId, Comment comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));
        comment.setPost(post);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        comment.setName(currentUser);

        if (comment.getId() != null) {
            Comment existingComment = commentRepository.findById(comment.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + comment.getId()));
            existingComment.setComment(comment.getComment());
            return commentRepository.save(existingComment);
        } else {
            return commentRepository.save(comment);
        }
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID: " + commentId));
        commentRepository.delete(comment);
    }
}


