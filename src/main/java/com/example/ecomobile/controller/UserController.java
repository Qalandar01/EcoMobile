package com.example.ecomobile.controller;

import com.example.ecomobile.dto.UserDTO;
import com.example.ecomobile.entity.Attachment;
import com.example.ecomobile.entity.AttachmentContent;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.repo.AttachmentContentRepository;
import com.example.ecomobile.repo.AttachmentRepository;
import com.example.ecomobile.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;

    @PutMapping(value = "/{userId}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateUserDetails(@PathVariable Integer userId,
                                               @RequestPart UserDTO userDTO,
                                               @RequestPart(required = false) MultipartFile profileImage) throws IOException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAge(userDTO.getAge());
        user.setGender(userDTO.getGender());

        if (profileImage != null) {
            Attachment attachment = Attachment.builder()
                    .filename(profileImage.getOriginalFilename())
                    .build();
            attachmentRepository.save(attachment);

            AttachmentContent attachmentContent = AttachmentContent.builder()
                    .attachment(attachment)
                    .content(profileImage.getBytes())
                    .build();
            attachmentContentRepository.save(attachmentContent);

            user.setAttachment(attachment);
        }

        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();
        UserDTO userDTO = UserDTO.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .age(user.getAge())
                .gender(user.getGender())
                .attachmentId(user.getAttachment() != null ? user.getAttachment().getId() : null)
                .build();

        return ResponseEntity.ok(userDTO);
    }
}