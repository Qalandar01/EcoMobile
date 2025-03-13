package com.example.ecomobile.service;

import com.example.ecomobile.dto.EmailConfirmationDTO;
import com.example.ecomobile.dto.EmailDTO;
import com.example.ecomobile.dto.ResetPasswordDTO;
import com.example.ecomobile.repo.UserRepository;
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
    private final UserRepository userRepository;

    public LoginService(EmailService emailService, RedisService redisService, UserRepository userRepository) {
        this.emailService = emailService;
        this.redisService = redisService;
        this.userRepository = userRepository;
    }

    public HttpEntity<?> sendConfirmationCode(@Valid EmailDTO email) {
        if (!userRepository.existsUsersByEmail(email.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

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
