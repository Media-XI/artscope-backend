package com.example.codebase.domain.artwork.document;

import jakarta.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "artworks", createIndex = false)
@Getter
public class ArtworkDocument {

    @Id
    private Long id;

    @Field(name = "title", type = FieldType.Text, analyzer = "my_nori_analyzer")
    private String title;

    @Field(name = "tags", type = FieldType.Text, analyzer = "my_nori_analyzer")
    private String tags;

    @Field(name = "description", type = FieldType.Text, analyzer = "my_nori_analyzer")
    private String description;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(name = "company_name",type = FieldType.Keyword)
    private String companyName;

    @Field(name = "company_role", type = FieldType.Keyword)
    private String companyRole;

    @Field(name = "media_url", type = FieldType.Keyword)
    private String mediaUrl;

    // TODO : visible 검색 조건 제외

    @Field(name = "created_time", type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime createdTime;

    @Field(name = "updated_time", type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime updatedTime;

    @Builder
    public ArtworkDocument(Long id, String title, String tags, String description, String name, String companyName, String companyRole, String mediaUrl, LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.description = description;
        this.name = name;
        this.companyName = companyName;
        this.companyRole = companyRole;
        this.mediaUrl = mediaUrl;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

}
