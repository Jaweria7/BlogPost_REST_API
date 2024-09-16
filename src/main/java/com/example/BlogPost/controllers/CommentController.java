package com.example.BlogPost.controllers;

import com.example.BlogPost.entities.Comment;
import com.example.BlogPost.services.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    //get comments
    @GetMapping("/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    //post comment
    @PostMapping("/{postId}")
    public ResponseEntity<Comment> saveComment(@PathVariable Long postId, @RequestBody Comment comment) {
        Comment savedComment = commentService.saveOrUpdateComment(postId, comment);
        return ResponseEntity.ok(savedComment);
    }

    //update comment
    @PreAuthorize("(hasRole('AUTHOR') and #vars.comment.name != null and #authentication.principal.username.toLowerCase() == #vars.comment.name.toLowerCase()) or hasRole('ADMIN')")
    @PutMapping("/{postId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long postId, @RequestBody Comment comment) {
        Comment savedComment = commentService.saveOrUpdateComment(postId, comment);
        return ResponseEntity.ok(savedComment);
    }

    //delete comment
    @PreAuthorize("(hasRole('AUTHOR') and #vars.comment.name != null and #authentication.principal.username.toLowerCase() == #vars.comment.name.toLowerCase()) or hasRole('ADMIN')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}