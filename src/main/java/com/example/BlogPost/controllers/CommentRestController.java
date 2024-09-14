package com.example.BlogPost.controllers;

import com.example.BlogPost.entities.Comment;
import com.example.BlogPost.entities.Post;
import com.example.BlogPost.services.CommentService;
import com.example.BlogPost.services.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    private final CommentService commentService;
    private final PostService postService;

    public CommentRestController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Comment> saveOrUpdateComment(@PathVariable Long postId, @RequestBody Comment comment) {
        Comment savedComment = commentService.saveOrUpdateComment(postId, comment);
        return ResponseEntity.ok(savedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
