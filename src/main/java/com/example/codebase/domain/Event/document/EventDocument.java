package com.example.codebase.domain.Event.document;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "events", createIndex = false)
@Getter
public class EventDocument {

    @Id
    private Long id;

    private String title;

    private String description;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(name = "company_name",type = FieldType.Keyword)
    private String companyName;

    @Field(name = "company_role", type = FieldType.Keyword)
    private String companyRole;

    @Field(name = "media_url", type = FieldType.Keyword)
    private String mediaUrl;

    @Field(name = "created_time", type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime createdTime;

    @Field(name = "updated_time", type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime updatedTime;

    @Builder
    public EventDocument(Long id, String title, String description, String name, String companyName, String companyRole, String mediaUrl, LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.name = name;
        this.companyName = companyName;
        this.companyRole = companyRole;
        this.mediaUrl = mediaUrl;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
}
