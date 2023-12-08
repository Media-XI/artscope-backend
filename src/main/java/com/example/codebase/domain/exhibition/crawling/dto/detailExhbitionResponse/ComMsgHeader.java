package com.example.codebase.domain.exhibition.crawling.dto.detailExhbitionResponse;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class ComMsgHeader{
    private String RequestMsgID;
    private String ResponseTime;
    private String ResponseMsgID;
    private String SuccessYN;
    private String ReturnCode;
    private String ErrMsg;
}
