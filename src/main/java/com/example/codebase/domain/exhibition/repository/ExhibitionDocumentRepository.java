package com.example.codebase.domain.exhibition.repository;

import com.example.codebase.domain.exhibition.document.ExhibitionDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ExhibitionDocumentRepository extends ElasticsearchRepository<ExhibitionDocument, Long> {
}
