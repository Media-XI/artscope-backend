package com.example.codebase.domain.Event.repository;

import com.example.codebase.domain.Event.document.EventDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@Profile({"dev", "prod"})
public interface EventDocumentRepository extends ElasticsearchRepository<EventDocument, Long> {
}
