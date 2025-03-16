package com.example.ecomobile.service;

import com.example.ecomobile.dto.EmailConfirmationDTO;
import com.example.ecomobile.dto.EmailDTO;
import com.example.ecomobile.dto.ResetPasswordDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final EmailService emailService;
    private final RedisService redisService;

    public LoginService(EmailService emailService, RedisService redisService) {
        this.emailService = emailService;
        this.redisService = redisService;
    }

    public HttpEntity<?> sendConfirmationCode(@Valid EmailDTO email) {
        try {
            String confirmationCode = emailService.generateRandomCode();
            emailService.sendConfirmationEmail(email.getEmail(),confirmationCode);
            redisService.saveCode(email.getEmail(),confirmationCode);
        } catch (MessagingException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    public HttpEntity<?> verifyCode(EmailConfirmationDTO resetPasswordDTO) {
        String sentCode = redisService.getCode(resetPasswordDTO.getEmail());
        if (resetPasswordDTO.getCode().equals(sentCode)){
            redisService.deleteCode(resetPasswordDTO.getEmail());
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.badRequest().build();
        }
    }
}
