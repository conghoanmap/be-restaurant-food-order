package com.restaurant.foodorder.controller;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.restaurant.foodorder.service.UploadImageFile;

@RestController
@RequestMapping("/api/file")
public class UploadFileController {

    private final UploadImageFile uploadImageFile;

    public UploadFileController(UploadImageFile uploadImageFile) {
        this.uploadImageFile = uploadImageFile;
    }

    @PostMapping("/upload-to-cloudinary")
    public ResponseEntity<?> uploadFileToCloudinary(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(uploadImageFile.uploadImage(file));
    }
}
