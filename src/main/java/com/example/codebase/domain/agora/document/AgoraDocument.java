package com.example.codebase.domain.agora.document;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "agoras", createIndex = false)
@Getter
public class AgoraDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "my_nori_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "my_nori_analyzer")
    private String content;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(name = "is_anonymous", type = FieldType.Boolean)
    private Boolean isAnonymous;

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
    public AgoraDocument(Long id, String title, String content, String name, String companyName, String companyRole, String mediaUrl, Boolean isAnonymous, LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.name = name;
        this.companyName = companyName;
        this.companyRole = companyRole;
        this.mediaUrl = mediaUrl;
        this.isAnonymous = isAnonymous;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
}
