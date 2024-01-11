package com.example.codebase.domain.artwork.repository;

import com.example.codebase.domain.artwork.document.ArtworkDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@Profile({"dev", "prod"})
public interface ArtworkDocumentRepository extends ElasticsearchRepository<ArtworkDocument, Long> {
}
