package com.example.codebase.domain.exhibition.crawling.dto.exhibitionResponse;

import com.example.codebase.discord.exhibition.data.EventDiscordMsgData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class XmlExhibitionDataLoadResult {
    private List<XmlExhibitionResponse> xmlExhibitionResponses;
    private EventDiscordMsgData eventDiscordMsgData;
}
