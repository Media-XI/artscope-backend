package com.example.codebase.domain.Event.repository;

import com.example.codebase.domain.Event.document.EventDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EventDocumentRepository extends ElasticsearchRepository<EventDocument, Long> {
}
