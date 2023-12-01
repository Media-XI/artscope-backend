package com.example.codebase.domain.exhibition.crawling.dto.exhibitionResponse;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "response")
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(propOrder = {"msgBody", "comMsgHeader"})
public class Response {
    private MsgBody msgBody;
    private ComMsgHeader comMsgHeader;
}



