package com.example.codebase.domain.magazine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MagazineRequest {

    @Getter
    @Setter
    @Schema(name = "MagazineRequest.Create", description = "매거진 생성 DTO")
    public static class Create {

        @NotEmpty
        private String title;

        @NotEmpty
        private String content;

        @NotNull
        private String categorySlug;

        // TODO: metadata 에 대해 검증 필요 (보안)
        @Schema(description = "JSON 형식의 메타데이터")
        private Map<String, String> metadata;

        @Size(max = 10, message = "최대 10개 까지 미디어 첨부 가능합니다.")
        private List<@URL(message = "올바른 URL 형식이 아닙니다.") String> mediaUrls = Collections.emptyList();

        @NotBlank(message = "URN을 입력해주세요.")
        @Pattern(regexp = "^urn:(member|team:\\w+)$", message = "올바른 URN 형식이 아닙니다.")
        private String urn;

    }

    @Getter
    @Setter
    @Schema(name = "MagazineRequest.Update", description = "매거진 수정 DTO")
    public static class Update {

        @NotEmpty
        private String title;

        @NotEmpty
        private String content;

        @NotNull
        private String categorySlug;

        private Map<String, String> metadata;

        private List<String> mediaUrls;
    }

    @Schema(name = "MagazineRequest.MagazineEntityUrn", description = "MagazineEntityUrn Request")
    public enum MagazineEntityUrn {
        MEMBER("urn:member"),
        TEAM("urn:team");

        private final String resource;

        @Getter String id;

        MagazineEntityUrn(String resource){
            this.resource = resource;
            this.id = null;
        }

        public static MagazineEntityUrn from (String urn){
            try {
                String[] checkSplit = urn.split(":");

                if (checkSplit.length == 2) {
                    return MagazineEntityUrn.valueOf(checkSplit[1].toUpperCase());
                } else if (checkSplit.length == 3) {
                    String resource = checkSplit[1];
                    String id = checkSplit[2];

                    MagazineEntityUrn magazineEntityUrn = MagazineEntityUrn.valueOf(resource.toUpperCase());
                    magazineEntityUrn.id = id;
                    return magazineEntityUrn;
                }
                else{
                    throw new RuntimeException("유효하지 않은 EntityUrn 입니다.");
                }
            }
            catch (IllegalArgumentException e){
                throw new RuntimeException("유효하지 않은 EntityUrn 입니다.");
            }
        }
    }
}
