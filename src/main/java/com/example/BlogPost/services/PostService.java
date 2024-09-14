package com.example.BlogPost.services;

import com.example.BlogPost.entities.Post;
import com.example.BlogPost.entities.Tag;
import com.example.BlogPost.repositories.PostRepository;
import com.example.BlogPost.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
    }

    public Post saveOrUpdatePost(Post postDetails, String tagsInput) {
        Post existingPost = null;

        if (postDetails.getId() != null) {
            existingPost = postRepository.findById(postDetails.getId())
                    .orElse(null);
        }

        if (existingPost != null) {
            existingPost.setTitle(postDetails.getTitle());
            existingPost.setContent(postDetails.getContent());
            existingPost.setExcerpt(postDetails.getExcerpt());
        } else {
            existingPost = new Post();
            existingPost.setTitle(postDetails.getTitle());
            existingPost.setContent(postDetails.getContent());
            existingPost.setExcerpt(postDetails.getExcerpt());
        }

        Set<Tag> tags = new HashSet<>();
        String[] tagNames = tagsInput.split(",");

        for (String tagName : tagNames) {
            tagName = tagName.trim();
            if (tagName.isEmpty()) {
                continue;
            }
            Tag tag = tagRepository.findByName(tagName);
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName);
                tagRepository.save(tag);
            }
            tags.add(tag);
        }
        existingPost.getTags().clear();
        existingPost.setTags(tags);

        return postRepository.save(existingPost);
    }

    public boolean deletePost(Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}

