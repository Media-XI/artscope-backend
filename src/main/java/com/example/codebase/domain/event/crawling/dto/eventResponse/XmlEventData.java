package com.example.codebase.domain.event.crawling.dto.eventResponse;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlEventData {
    private long seq;
    private String title;
    private String startDate;
    private String endDate;
    private String place;
    private String realmName;
    private String area;
    private String thumbnail;
    private double gpsX;
    private double gpsY;
}

