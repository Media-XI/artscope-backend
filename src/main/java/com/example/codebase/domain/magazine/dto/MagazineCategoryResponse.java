package com.example.codebase.domain.magazine.dto;

import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MagazineCategoryResponse {

    @Getter
    @Setter
    @Schema(name = "MagazineCategoryResponse.Get", description = "Category Response")
    public static class Get {

        private Long id;

        private String name;

        private String slug;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedTime;

        private List<Get> childrenCategories = new ArrayList<>();

        private Create parentCategory = null;

        public static MagazineCategoryResponse.Get from(MagazineCategory category) {
            Get get = new Get();
            get.setId(category.getId());
            get.setName(category.getName());
            get.setSlug(category.getSlug());
            get.setCreatedTime(category.getCreatedTime());
            get.setUpdatedTime(category.getUpdatedTime());
            if (category.getChildren() != null) {
                get.setChildrenCategories(category.getChildren().stream().map(Get::from).toList());
            }

            if (category.getParent() != null) {
                get.setParentCategory(Create.from(category.getParent()));
            }

            return get;
        }
    }


    @Getter
    @Setter
    @Schema(name = "MagazineCategoryResponse.Create", description = "Category Response Create")
    public static class Create {

        private Long id;

        private String name;

        private String slug;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedTime;

        private Create parentCategory = null;
        public static MagazineCategoryResponse.Create from(MagazineCategory category) {
            Create create = new Create();
            create.setId(category.getId());
            create.setName(category.getName());
            create.setSlug(category.getSlug());
            create.setCreatedTime(category.getCreatedTime());
            create.setUpdatedTime(category.getUpdatedTime());

            if (category.getParent() != null) {
                create.setParentCategory(Create.from(category.getParent()));
            }
            return create;
        }
    }

    @Getter
    @Setter
    @Schema(name = "MagazineCategoryRequest", description = "MagazineCategoryRequest")
    public static class GetAll {

        private List<Get> categories;

        public static GetAll from(List<MagazineCategory> all) {
            GetAll getAll = new GetAll();
            getAll.setCategories(all.stream()
                    .map(Get::from)
                    .toList());
            return getAll;
        }
    }

}
