package com.example.codebase.domain.feed.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedResponseDto {

    private List<FeedItemResponseDto> feedItems;

    private boolean hasNext;


    public static FeedResponseDto of(List<FeedItemResponseDto> feedItems, boolean hasNext) {
        FeedResponseDto feedResponseDto = new FeedResponseDto();
        feedResponseDto.setFeedItems(feedItems);
        feedResponseDto.setHasNext(hasNext);
        return feedResponseDto;
    }

}
