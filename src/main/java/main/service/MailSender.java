package main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSender {
    final static String SUBJECT = "Password reset";
    final static String EMAIL_FROM = "fo4test@yandex.ru";
    @Autowired
    private JavaMailSender mailSender;

    public void send(String emailTo, String message){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(EMAIL_FROM);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(SUBJECT);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }
}
