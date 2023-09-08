package com.example.codebase.domain.feed.dto;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.artwork.entity.Artwork;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeedResponseDto {

    private List<FeedItemResponseDto> feedItems;

    private PageInfo pageInfo;

    private boolean hasNext;


    public static FeedResponseDto of(List<FeedItemResponseDto> feedItems, PageInfo pageInfo) {
        FeedResponseDto feedResponseDto = new FeedResponseDto();
        feedResponseDto.setFeedItems(feedItems);
        feedResponseDto.setPageInfo(pageInfo);

        boolean hasNext = (pageInfo.getSize() * 3L) * pageInfo.getPage() < pageInfo.getTotalElements();

        feedResponseDto.setHasNext(hasNext);

        return feedResponseDto;
    }

}
