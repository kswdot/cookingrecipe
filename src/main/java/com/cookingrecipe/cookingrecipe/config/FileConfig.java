package com.cookingrecipe.cookingrecipe.config;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileConfig {

    private static final String UPLOAD_DIR = "C:/Users/user/.gradle/cookingrecipe/uploaded-images"; // 절대 경로

    public static String createUploadDir() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            return uploadPath.toAbsolutePath().toString(); // 업로드 경로 반환
        } catch (Exception e) {
            throw new IllegalStateException("업로드 폴더를 생성하는 중 문제가 발생했습니다.", e);
        }
    }
}
