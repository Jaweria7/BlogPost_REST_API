package com.example.BlogPost.services;

import com.example.BlogPost.entities.FilterCriteria;
import com.example.BlogPost.entities.Post;
import com.example.BlogPost.entities.Tag;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecification implements Specification<Post> {

    private final FilterCriteria criteria;
    private final String searchQuery;

    public PostSpecification(FilterCriteria criteria, String searchQuery) {
        this.criteria = criteria;
        this.searchQuery = searchQuery;
    }

    @Override
    public Predicate toPredicate(Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        if (searchQuery != null && !searchQuery.isEmpty()) {
            String searchLower = searchQuery.toLowerCase();
            Predicate titlePredicate = cb.like(cb.lower(root.get("title")), "%" + searchLower + "%");
            Predicate contentPredicate = cb.like(cb.lower(root.get("content")), "%" + searchLower + "%");
            Predicate excerptPredicate = cb.like(cb.lower(root.get("excerpt")), "%" + searchLower + "%");
            Predicate authorPredicate = cb.like(cb.lower(root.get("author")), "%" + searchLower + "%");

            Join<Post, Tag> tagsJoin = root.join("tags");
            Predicate tagPredicate = cb.like(cb.lower(tagsJoin.get("name")), "%" + searchLower + "%");

            Predicate searchPredicate = cb.or(titlePredicate, contentPredicate, excerptPredicate, authorPredicate, tagPredicate);
            predicate = cb.and(predicate, searchPredicate);
        }

        if (criteria != null) {
            if (criteria.getAuthors() != null && !criteria.getAuthors().isEmpty()) {
                predicate = cb.and(predicate, root.get("author").in(criteria.getAuthors()));
            }

            if (criteria.getTags() != null && !criteria.getTags().isEmpty()) {
                Join<Post, Tag> tagsJoin = root.join("tags");
                predicate = cb.and(predicate, tagsJoin.get("name").in(criteria.getTags()));
            }

            if (criteria.getStartDate() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("publishedAt"), criteria.getStartDate()));
            }

            if (criteria.getEndDate() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("publishedAt"), criteria.getEndDate()));
            }
        }
        return predicate;
    }
}
