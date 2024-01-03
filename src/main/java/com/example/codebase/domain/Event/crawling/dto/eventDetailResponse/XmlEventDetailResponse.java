package com.example.codebase.domain.Event.crawling.dto.eventDetailResponse;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

import java.io.StringReader;

@Getter
@Setter
@XmlRootElement(name = "response")
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(propOrder = {"msgBody", "comMsgHeader"})
public class XmlEventDetailResponse {
    private MsgBody msgBody;
    private ComMsgHeader comMsgHeader;

    public static XmlEventDetailResponse parse(String body)  {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(XmlEventDetailResponse.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            StringReader reader = new StringReader(body);
            return (XmlEventDetailResponse) unmarshaller.unmarshal(reader);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public XmlEventDetailData getDetailExhibitionData() {
        return msgBody.getDetailExhibitionData();
    }
}