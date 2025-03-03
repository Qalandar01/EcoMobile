package com.example.ecomobile.controller;

import com.example.ecomobile.entity.Attachment;
import com.example.ecomobile.entity.AttachmentContent;
import com.example.ecomobile.repo.AttachmentContentRepository;
import com.example.ecomobile.repo.AttachmentRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class AttachmentController {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;

    public AttachmentController(AttachmentRepository attachmentRepository, AttachmentContentRepository attachmentContentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
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



}

