package com.example.ecomobile.controller;

import com.example.ecomobile.entity.Attachment;
import com.example.ecomobile.entity.AttachmentContent;
import com.example.ecomobile.repo.AttachmentContentRepository;
import com.example.ecomobile.repo.AttachmentRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@RestController
@RequestMapping("/api/file")
public class UploadController {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;

    public UploadController(AttachmentRepository attachmentRepository,
                            AttachmentContentRepository attachmentContentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Integer id) {
        AttachmentContent attachmentContent = attachmentContentRepository.findByAttachment_Id(id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(attachmentContent.getContent());
    }


    @PostMapping
    public Attachment uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Attachment attachment = new Attachment();
        attachment.setFilename(file.getOriginalFilename());

        attachmentRepository.save(attachment);

        AttachmentContent attachmentContent = new AttachmentContent();
        attachmentContent.setAttachment(attachment);
        attachmentContent.setContent(file.getBytes());

        attachmentContentRepository.save(attachmentContent);
        return attachment;
    }
}


