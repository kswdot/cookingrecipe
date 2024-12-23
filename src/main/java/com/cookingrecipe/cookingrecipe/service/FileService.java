package com.cookingrecipe.cookingrecipe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    public String saveFile(MultipartFile file, String uploadDir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID() + fileExtension;

        Path filePath = Paths.get(uploadDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath);

        log.warn("File saved successfully at path: {}", filePath.toAbsolutePath());

        return fileName;
    }

    public String updateFile(MultipartFile file, String uploadDir, String existingFilePath) throws IOException {
        log.warn("Entering updateFile method");
        log.warn("Upload Directory: {}", uploadDir);
        log.warn("Existing File Path: {}", existingFilePath);

        if (file == null || file.isEmpty()) {
            log.warn("No new file uploaded. Keeping existing file: {}", existingFilePath);
            return existingFilePath;
        }

        // 기존 파일 삭제
        if (existingFilePath != null) {
            Path existingPath = Paths.get(uploadDir, existingFilePath);
            Files.deleteIfExists(existingPath);
            log.warn("Deleted existing file: {}", existingPath);
        }

        // 새 파일 저장
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID() + fileExtension;

        Path filePath = Paths.get(uploadDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath);
        log.warn("File copied to: {}", filePath.toAbsolutePath());
        log.warn("Saved new file: {}", filePath);

        return fileName;
    }

    public void deleteFile(String uploadDir, String filePath) throws IOException {
        if (filePath != null) {
            Path path = Paths.get(uploadDir, filePath);
            Files.deleteIfExists(path);
            log.warn("Deleted file: {}", path.toAbsolutePath());
        }
    }
}
