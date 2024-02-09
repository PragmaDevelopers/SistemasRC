package com.api.sistema_rc.service;

import com.api.sistema_rc.model.User;
import com.api.sistema_rc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;
    public void sendMail(String mail,String subject,String message){
        User user = userRepository.findByEmail(mail).get();
        if(user.isReceiveNotification()){
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(fromEmail);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(message);
            simpleMailMessage.setTo(mail);
            mailSender.send(simpleMailMessage);
        }
    }
}
