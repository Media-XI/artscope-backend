package com.example.codebase.domain.post.repository;

import com.example.codebase.domain.post.document.PostDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@Profile({"dev", "prod"})
public interface PostDocumentRepository extends ElasticsearchRepository<PostDocument, Long> {
}
