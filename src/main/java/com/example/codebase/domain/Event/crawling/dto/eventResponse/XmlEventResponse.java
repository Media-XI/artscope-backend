package com.example.codebase.domain.Event.crawling.dto.eventResponse;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.StringReader;
import java.util.List;

@Log4j2
@Getter
@Setter
@XmlRootElement(name = "response")
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(propOrder = {"msgBody", "comMsgHeader"})
public class XmlEventResponse {
    private MsgBody msgBody;

    private ComMsgHeader comMsgHeader;

    public static XmlEventResponse parse(String body) {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(XmlEventResponse.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(body);
            return (XmlEventResponse) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public void statusCheck() {
        switch (this.getComMsgHeader().getReturnCode()) {
            case "00" -> {}
            case "30" -> throw new RuntimeException("서비스 키 관련 문제");
            case "22" -> throw new RuntimeException("API 호출 횟수 초과");
            default -> throw new RuntimeException("이벤트가 조회 되지 않음");
        }
    }
    public List<XmlEventData> getXmlExhibitions() {
        return this.msgBody.getXmlEventData();
    }
}




