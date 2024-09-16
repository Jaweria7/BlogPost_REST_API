package com.example.BlogPost.controllers;

import com.example.BlogPost.entities.FilterCriteria;
import com.example.BlogPost.entities.Post;
import com.example.BlogPost.services.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // View all posts with pagination, sorting, and filtering
    @GetMapping
    public ResponseEntity <?> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) List<String> authors,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        FilterCriteria criteria = new FilterCriteria();
        criteria.setAuthors(authors);
        criteria.setTags(tags);
        criteria.setStartDate(startDate);
        criteria.setEndDate(endDate);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts = postService.searchAndFilterPosts(searchQuery, criteria, pageable);

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    // View post by ID
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }

    // Save a new post
    @PostMapping
    public ResponseEntity<Post> savePost(@RequestBody Post post){
        postService.savePost(post);
        return ResponseEntity.ok(post);
    }

    // Update an existing post
    @PreAuthorize("(hasRole('AUTHOR') and #post.author != null and #authentication.principal.username.toLowerCase() == #post.author.toLowerCase()) or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post){
        post.setId(id);
        postService.updatePostWithTags(post);
        return ResponseEntity.ok(post);
    }

    // Delete an existing post
    @PreAuthorize("(hasRole('AUTHOR') and #post.author != null and #authentication.principal.username.toLowerCase() == #post.author.toLowerCase()) or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePostById(id);
        return ResponseEntity.noContent().build();
    }
}
