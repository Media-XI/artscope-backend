package com.example.codebase.domain.exhibition.crawling.dto.exhibitionResponse;

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
    private List<XmlExhibitionData> xmlExhibitionData;
}
