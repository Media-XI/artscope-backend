package com.example.codebase.controller.dto;

import com.example.codebase.domain.search.type.SearchSortType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestMaker {
    public static PageRequest makePageRequest(int page, int size, String sortType) {
        SearchSortType searchSortType = SearchSortType.create(sortType);
        Sort sort = Sort.by(Sort.Direction.DESC, searchSortType.getValue());
        return PageRequest.of(page, size, sort);
    }
}
