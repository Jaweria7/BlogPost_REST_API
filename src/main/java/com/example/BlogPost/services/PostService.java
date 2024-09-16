package com.example.BlogPost.services;

import com.example.BlogPost.entities.FilterCriteria;
import com.example.BlogPost.entities.Post;
import com.example.BlogPost.entities.Tag;
import com.example.BlogPost.repositories.PostRepository;
import com.example.BlogPost.repositories.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public Page<Post> searchAndFilterPosts(String searchQuery, FilterCriteria criteria, Pageable pageable) {
        Specification<Post> specification = new PostSpecification(criteria, searchQuery);
        return postRepository.findAll(specification, pageable);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public void savePost(Post post) {
        Set<Tag> tags = post.getTagList().stream().map(tagString -> {
            Tag tag = tagRepository.findByName(tagString);
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagString);
                tagRepository.save(tag);
            }
            return tag;
        }).collect(Collectors.toSet());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        post.setAuthor(currentUser);
        post.setTags(tags);
        postRepository.save(post);
    }

    public void updatePostWithTags(Post post) {
        Post existingPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + post.getId()));

        existingPost.getTags().clear();
        Set<Tag> tags = post.getTagList().stream().map(tagString -> {
            Tag tag = tagRepository.findByName(tagString);
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagString);
                tagRepository.save(tag);
            }
            return tag;
        }).collect(Collectors.toSet());

        existingPost.setTags(tags);
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());
        existingPost.setExcerpt(post.getExcerpt());

        postRepository.save(existingPost);
    }

    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }
}
