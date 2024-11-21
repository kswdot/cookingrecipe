package com.cookingrecipe.cookingrecipe.config;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Slf4j
// 업로드 폴더가 없으면 생성하는 메서드
public class FileService {


    private static final String UPLOAD_DIR = "uploads/";

    public static void createUploadDir() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) { // 업로드 폴더가 없으면
                Files.createDirectories(uploadPath); // 폴더 생성
            }
        } catch (Exception e) {
            throw new IllegalStateException("업로드 폴더를 생성하는 중 문제가 발생했습니다.", e);
        }
    }
}
