package com.example.ecomobile.controller;

import com.example.ecomobile.dto.EmailConfirmationDTO;
import com.example.ecomobile.dto.EmailDTO;
import com.example.ecomobile.dto.RegisterDTO;
import com.example.ecomobile.service.RegisterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class RegisterController {
    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register-mail")
    public HttpEntity<?> register(@Valid @RequestBody EmailDTO emailDTO) {
       return registerService.sendMailCode(emailDTO);
    }

    @PostMapping("/verification")
    public HttpEntity<?> verification(@RequestBody RegisterDTO registerDTO) {
        return registerService.testCode(registerDTO);
    }

}
