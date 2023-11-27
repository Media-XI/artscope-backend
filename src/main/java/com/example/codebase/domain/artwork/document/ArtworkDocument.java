package com.example.codebase.domain.artwork.document;

import jakarta.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "artworks")
@Getter
public class ArtworkDocument {

    @Id
    @Field(name = "artwork_id")
    private Long id;

    @Field(name = "title", type = FieldType.Text, analyzer = "my_nori_analyzer")
    private String title;

    @Field(name = "tags", type = FieldType.Text, analyzer = "my_nori_analyzer")
    private String tags;

    @Field(name = "description", type = FieldType.Text, analyzer = "my_nori_analyzer")
    private String description;

    // TODO : visible 검색 조건 제외

    @Field(name = "created_time", type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdTime;

    @Field(name = "updated_time", type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updatedTime;

    @Builder
    private ArtworkDocument(Long id, String title, String tags, String description, LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.description = description;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

}
