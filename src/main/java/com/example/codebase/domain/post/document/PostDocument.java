package com.example.codebase.domain.post.document;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "posts")
@Getter
public class PostDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "my_nori_analyzer")
    private String content;

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

    public PostDocument(Long id, String content, String name, String companyName, String companyRole, String mediaUrl, LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.id = id;
        this.content = content;
        this.name = name;
        this.companyName = companyName;
        this.companyRole = companyRole;
        this.mediaUrl = mediaUrl;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }
}
