package main.service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Setter
@ConfigurationProperties(prefix = "mail")
public class MailSender {
    final static String SUBJECT = "Password reset";
    public String username;
    @Autowired
    private JavaMailSender mailSender;

    public void send(String emailTo, String message){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(SUBJECT);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }
}
