package com.example.internship.service.impl;

import com.example.internship.config.GlobalConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SendMailService {

    private final GlobalConfig globalConfig;

    public void send(String emailTo, String emailFrom) throws MessagingException {
        String to = emailTo;

        String from = emailFrom;

        var host = globalConfig.hostMail();

        var dateTime = LocalDateTime.now();

        var properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", globalConfig.getPortMail());

        var session = Session.getInstance(properties);

        var message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("Password Updated");
        message.setText("Password updated at "+ dateTime);
        Transport.send(message);
    }

}
