package com.example.codebase.domain.exhibition.crawling.dto.detailExhbitionResponse;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlDetailExhibitionData {
    private Long seq;
    private String title;
    private String startDate;
    private String endDate;
    private String place;
    private String realmName;
    private String area;
    private String subTitle;
    private String price;
    private String contents1;
    private String contents2;
    private String url;
    private String phone;
    private String imgUrl;
    private String gpsX;
    private String gpsY;
    private String placeUrl;
    private String placeAddr;
    private String placeSeq;
}
