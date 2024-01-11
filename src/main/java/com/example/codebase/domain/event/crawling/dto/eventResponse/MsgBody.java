package com.example.codebase.domain.event.crawling.dto.eventResponse;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class MsgBody {
    private int totalCount;
    private int cPage;
    private int rows;
    private String from;
    private String to;
    private int sortStdr;

    @XmlElement(name = "perforList")
    private List<XmlEventData> xmlEventData;
}
