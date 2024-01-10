package com.example.codebase.domain.agora.repository;

import com.example.codebase.domain.agora.document.AgoraDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@Profile({"dev", "prod"})
public interface AgoraDocumentRepository extends ElasticsearchRepository<AgoraDocument, Long>{
}
