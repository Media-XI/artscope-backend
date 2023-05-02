package com.example.codebase.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ClientUtil {

    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        // Proxy
        if (ip == null || ip.length() == 0) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        // Weblogic
        if (ip == null || ip.length() == 0) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ip == null || ip.length() == 0) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ip == null || ip.length() == 0) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    public static StringBuilder jsonBodyForLogging(Object body) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder("Body = \n");
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String jsonInString = null;

        if (body == null) {
            return stringBuilder.append("null");
        }

        try
        {
            jsonInString = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(body)
                    .replaceAll("\"password\" : \"[^\"]*\"", "\"password\" : \"*****\"")
                    .replaceAll("\"accessToken\" : \"[^\"]*\"", "\"accessToken\" : \"*****\"")
                    .replaceAll("\"refreshToken\" : \"[^\"]*\"", "\"refreshToken\" : \"*****\"");

        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e.getMessage());
        }

        if (jsonInString.length() > 1000) {
            stringBuilder
                    .append(jsonInString.substring(0, 500))
                    .append("\n\n...\n\n")
                    .append(jsonInString.substring(jsonInString.length() - 500));
        } else {
            stringBuilder.append(jsonInString);
        }

        return stringBuilder;
    }

}
