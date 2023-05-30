package com.example.codebase.domain.mail.service;

import com.example.codebase.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private final String CODE_SERVER = "http://localhost:8080/api/mail/authenticate";

    @Autowired
    public MailService(JavaMailSender javaMailSender, RedisUtil redisUtil) {
        this.javaMailSender = javaMailSender;
        this.redisUtil = redisUtil;
    }

    public void sendMail(String email) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[ArtScope] 이메일 인증번호");

            UUID code = UUID.randomUUID();
            redisUtil.setDataAndExpire(String.valueOf(code), email, 60 * 1000 * 5);

            StringBuilder sb = new StringBuilder();

            sb.append("<img src=\"");
            sb.append(randomUrl());
            sb.append("\"/>");
            sb.append("<h1>이메일 인증</h1>");
            sb.append("<h3>아래의 링크를 접속해주세요. </3>");
            sb.append("<h3>인증링크: ");
            sb.append("<a href=\"");
            sb.append(CODE_SERVER + "?code=" + code.toString());
            sb.append("\">");
            sb.append(CODE_SERVER + "?code=" + code.toString());
            sb.append("</a></h3>");
            sb.append("<h3>인증링크는 5분간 유효합니다.</h3>");

            mimeMessageHelper.setText(sb.toString(), true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer generateRandomNumber() {
        return (int) (Math.random() * 999999) + 100000;
    }


    private String randomUrl() {
        int num = (int) (Math.random() * 3);
        StringBuilder sb = new StringBuilder();
        sb.append("https://d14sxnpwbfro1f.cloudfront.net/email_logo_");
        sb.append(num);
        sb.append(".jpeg?w=300&f=webp&q=50");
        return sb.toString();
    }
}
