package com.example.codebase.controller.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Setter
@Getter
public class PageInfo {
    private int page;

    private int size;

    private int totalPages;

    private long totalElements;

    public static PageInfo from(Page page) {
        return of(page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalElements());
    }

    public static PageInfo of(int page, int size, int totalPages, long totalElements) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page);
        pageInfo.setSize(size);
        pageInfo.setTotalPages(totalPages);
        pageInfo.setTotalElements(totalElements);
        return pageInfo;
    }

    public static PageInfo of(int page, int size, long totalElements) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page);
        pageInfo.setSize(size);
        pageInfo.setTotalPages((int) Math.ceil((double) totalElements / size));
        pageInfo.setTotalElements(totalElements);
        return pageInfo;
    }

}
