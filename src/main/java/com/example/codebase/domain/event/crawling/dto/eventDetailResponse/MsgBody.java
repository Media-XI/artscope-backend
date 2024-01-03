package com.example.codebase.domain.event.crawling.dto.eventDetailResponse;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class MsgBody {
    private Long seq;

    @XmlElement(name = "perforInfo")
    private XmlEventDetailData detailExhibitionData;
}

