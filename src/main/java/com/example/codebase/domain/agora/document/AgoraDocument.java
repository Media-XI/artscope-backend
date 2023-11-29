package com.example.codebase.domain.agora.document;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "agoras")
@Getter
public class AgoraDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "my_nori_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "my_nori_analyzer")
    private String content;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime createdTime;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime updatedTime;

    @Builder
    public AgoraDocument(Long id, String title, String content, LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
}
