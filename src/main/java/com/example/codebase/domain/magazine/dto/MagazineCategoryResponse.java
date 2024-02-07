package com.example.codebase.domain.magazine.dto;

import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class MagazineCategoryResponse {

    @Getter
    @Setter
    @Schema(name = "MagazineCategoryResponse", description = "Category Response")
    public static class Get {

        private Long id;

        private String name;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedTime;

        public static MagazineCategoryResponse.Get from(MagazineCategory category) {
            Get get = new Get();
            get.setId(category.getId());
            get.setName(category.getName());
            get.setCreatedTime(category.getCreatedTime());
            get.setUpdatedTime(category.getUpdatedTime());
            return get;
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
