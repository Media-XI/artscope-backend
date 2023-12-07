package com.example.codebase.domain.exhibition.crawling.dto.detailExhbitionResponse;

import com.example.codebase.discord.exhibition.data.EventDiscordMsgData;
import lombok.Getter;

import java.util.List;

@Getter
public class XmlDetailExhibitionDataLoadResult {
    private final List<XmlDetailExhibitionResponse> xmlDetailExhibitionResponses;
    private final EventDiscordMsgData eventDiscordMsgData;

    public XmlDetailExhibitionDataLoadResult(List<XmlDetailExhibitionResponse> xmlDetailExhibitionResponses, EventDiscordMsgData eventDiscordMsgData) {
        this.xmlDetailExhibitionResponses = xmlDetailExhibitionResponses;
        this.eventDiscordMsgData = eventDiscordMsgData;
    }
}
