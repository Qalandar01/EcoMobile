package com.example.ecomobile.service;

import com.example.ecomobile.dto.EmailDTO;
import com.example.ecomobile.dto.RegisterDTO;
import com.example.ecomobile.entity.Role;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.enums.Gender;
import com.example.ecomobile.enums.RoleName;
import com.example.ecomobile.repo.RoleRepository;
import com.example.ecomobile.repo.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public RegisterService(UserRepository userRepository, EmailService emailService, RedisService redisService, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.redisService = redisService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public HttpEntity<?> sendMailCode(@Valid EmailDTO emailDTO) {
        if (userRepository.existsUsersByEmail(emailDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Bu email allaqachon ro'yxatdan o'tgan");
        }
        try {
            String confirmationCode = emailService.generateRandomCode();
            emailService.sendConfirmationEmail(emailDTO.getEmail(),confirmationCode);
            redisService.saveCode(emailDTO.getEmail(),confirmationCode);
        } catch (MessagingException e) {
            return ResponseEntity.badRequest().body("Kod yuborishda xatolik!");
        }
        return ResponseEntity.ok().body("Tasdiqlash kodi yuborildi!");
    }

    public HttpEntity<?> testCode(@Valid RegisterDTO emailConfirmationDTO) {
        String code = redisService.getCode(emailConfirmationDTO.getEmail());
        System.out.println("code = " + code);
        if (code.equals(emailConfirmationDTO.getCode())){
            System.out.println("kod to'g'ri");
            redisService.deleteCode(emailConfirmationDTO.getEmail());
            System.out.println("emailConfirmationDTO = " + emailConfirmationDTO);
            Gender gender = Gender.valueOf(emailConfirmationDTO.getGender().toUpperCase());
            List<Role> roleName = roleRepository.findByRoleName(RoleName.ROLE_USER);
            User user=User.builder()
                    .email(emailConfirmationDTO.getEmail())
                    .password(passwordEncoder.encode(emailConfirmationDTO.getPassword()))
                    .phone(emailConfirmationDTO.getPhone())
                    .age(emailConfirmationDTO.getAge())
                    .firstname(emailConfirmationDTO.getFirstName())
                    .lastname(emailConfirmationDTO.getLastName())
                    .gender(gender)
                    .roles(roleName)
                    .build();
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }else {
            System.out.println("kod xatolik");
            return ResponseEntity.badRequest().build();
        }
    }
}
