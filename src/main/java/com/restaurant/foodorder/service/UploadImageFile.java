package com.restaurant.foodorder.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.restaurant.foodorder.dto.APIResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UploadImageFile {

    @Autowired
    private Cloudinary cloudinary;

    public APIResponse<String> uploadImage(MultipartFile file) throws IOException {
        assert file.getOriginalFilename() != null;
        String publicValue = generatePublicValue(file.getOriginalFilename());
        log.info("Public value: " + publicValue);
        String extension = getFileName(file.getOriginalFilename())[1];
        log.info("Extension: " + extension);
        File fileUpload = convert(file);
        log.info("File upload: " + fileUpload);
        cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap("public_id", publicValue));
        cleanDisk(fileUpload);
        String filePath = cloudinary.url().generate(StringUtils.join(publicValue, ".", extension));
        return new APIResponse<>(200, "Upload image successfully", filePath);
    }

    private File convert(MultipartFile file) throws IOException {
        assert file.getOriginalFilename() != null;
        File convFile = new File(
                StringUtils.join(file.getOriginalFilename(), "_", getFileName(file.getOriginalFilename())[1]));
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, convFile.toPath());
        }
        return convFile;
    }

    private void cleanDisk(File file) {
        try {
            log.info("File path: " + file.toPath());
            Path filePath = file.toPath();
            Files.delete(filePath);
        } catch (Exception e) {
            log.error("Error when delete file: {}", e.getMessage());
        }
    }

    public String generatePublicValue(String originalName) {
        String fileName = getFileName(originalName)[0];
        return StringUtils.join(UUID.randomUUID().toString(), "_", fileName);
    }

    public String[] getFileName(String originalName) {
        return originalName.split("\\.");
    }

}
