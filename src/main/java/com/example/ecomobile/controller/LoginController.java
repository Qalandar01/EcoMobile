package com.example.ecomobile.controller;

import com.example.ecomobile.config.security.CustomUserDetails;
import com.example.ecomobile.dto.EmailDTO;
import com.example.ecomobile.dto.LoginDTO;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.service.JwtService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;


    public LoginController(AuthenticationManager authenticationManager, JwtService jwtService, JavaMailSender mailSender) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

        this.mailSender = mailSender;
    }

    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody LoginDTO loginDTO) {
        System.out.println("loginDTO = " + loginDTO);

        var authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);


        CustomUserDetails customUserDetails = (CustomUserDetails) authenticate.getPrincipal();
        User user = customUserDetails.getUser();
        System.out.println(user.getRoles().toString());

        return ResponseEntity.ok(jwtService.generateToken(user));
    }

    @PostMapping("/forgot-password")
    public HttpEntity<?> sendEmail(@RequestBody EmailDTO email) {
        System.out.println("email = " + email.getEmail());

        Random random = new Random();
        int resetCode = 100000 + random.nextInt(999999);

        // Send email with reset code
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email.getEmail());
        simpleMailMessage.setSubject("Password Reset Code");
        simpleMailMessage.setText("Your password reset code is: " + resetCode);


        mailSender.send(simpleMailMessage);

        System.out.println("Reset code sent: " + resetCode);

        // Schedule a task to expire the code after 1 minute
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            // Logic to expire the reset code after 1 minute
            System.out.println("Reset code expired after 1 minute.");
        }, 1, TimeUnit.MINUTES);

        return ResponseEntity.ok("A reset code has been sent to your email.");
    }



}
