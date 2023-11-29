package com.example.codebase.domain.exhibition.document;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@Document(indexName = "exhibitions")
@Getter
public class ExhibitionDocument {

    @Id
    private Long id;

    private String title;

    private String description;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @Builder
    public ExhibitionDocument(Long id, String title, String description, LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
}
