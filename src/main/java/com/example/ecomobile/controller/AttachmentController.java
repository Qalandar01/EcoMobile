package com.example.ecomobile.controller;

import com.example.ecomobile.entity.Attachment;
import com.example.ecomobile.entity.AttachmentContent;
import com.example.ecomobile.repo.AttachmentContentRepository;
import com.example.ecomobile.repo.AttachmentRepository;
import com.example.ecomobile.repo.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/file")
public class AttachmentController {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;
    private final UserRepository userRepository;

    public AttachmentController(AttachmentRepository attachmentRepository, AttachmentContentRepository attachmentContentRepository, UserRepository userRepository) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Integer upload(@RequestParam MultipartFile multipartFile) throws IOException {
     Attachment attachment = Attachment.builder()
             .filename(multipartFile.getOriginalFilename())
             .build();
     attachmentRepository.save(attachment);

        AttachmentContent attachmentContent = AttachmentContent.builder()
             .attachment(attachment)
             .content(multipartFile.getBytes())
             .build();

     attachmentContentRepository.save(attachmentContent);

     return attachment.getId();
     }

    @GetMapping("/{attachmentId}")
    public void getFile(@PathVariable Integer attachmentId, HttpServletResponse response) throws IOException {
        AttachmentContent byAttachmentId = attachmentContentRepository.findByAttachmentId(attachmentId);
        response.getOutputStream().write(byAttachmentId.getContent());
    }

    @GetMapping("/user/{userId}")
    public void getFileByUserId(@PathVariable Integer userId, HttpServletResponse response) throws IOException {
        Optional<Integer> attachmentIdOptional = userRepository.findAttachmentIdByUserId(userId);
        if (attachmentIdOptional.isPresent()) {
            AttachmentContent byAttachmentId = attachmentContentRepository.findByAttachmentId(attachmentIdOptional.get());
            response.setContentType("image/jpeg");
            response.getOutputStream().write(byAttachmentId.getContent());
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }



}

