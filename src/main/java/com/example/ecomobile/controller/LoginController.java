package com.example.ecomobile.controller;

import com.example.ecomobile.config.security.CustomUserDetails;
import com.example.ecomobile.dto.EmailDTO;
import com.example.ecomobile.dto.LoginDTO;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.service.JwtService;
import com.example.ecomobile.service.LoginService;
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
    private final LoginService loginService;


    public LoginController(AuthenticationManager authenticationManager, JwtService jwtService, JavaMailSender mailSender, LoginService loginService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

        this.mailSender = mailSender;
        this.loginService = loginService;
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

//    @PostMapping("/forgot-password")
//    public HttpEntity<?> sendEmail(@RequestBody EmailDTO email) {
//        loginService.
//    }



}
