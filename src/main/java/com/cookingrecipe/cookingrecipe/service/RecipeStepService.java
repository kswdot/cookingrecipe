package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.RecipeStep;
import com.cookingrecipe.cookingrecipe.dto.RecipeStepDto;
import com.cookingrecipe.cookingrecipe.repository.RecipeStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeStepService {


    private final RecipeStepRepository recipeStepRepository;


    // 레시피 단계 저장
    public void save(Board board, List<RecipeStepDto> recipeStepDto) throws IOException {
        String uploadDir = System.getProperty("user.dir") + "/uploaded-images/";

        for (RecipeStepDto stepDto : recipeStepDto) {
            String fileName = null;

            if (stepDto.getImage() != null && !stepDto.getImage().isEmpty()) {

                // 원본 파일 이름 가져오기
                String originalFileName = stepDto.getImage().getOriginalFilename();

                // 파일 이름이 null이거나 확장자가 없는 경우 기본 확장자를 추가
                String fileExtension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                } else {
                    // 기본 확장자 추가 (예: .jpg)
                    fileExtension = ".jpg";
                }

                // 파일 이름 생성
                fileName = UUID.randomUUID() + "_" + originalFileName;

                // 확장자 누락 시 추가
                if (!fileName.endsWith(fileExtension)) {
                    fileName += fileExtension;
                }

                Path filePath = Paths.get(uploadDir + fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(stepDto.getImage().getInputStream(), filePath);
            }

            // RecipeStep 생성 및 저장
            RecipeStep recipeStep = RecipeStep.builder()
                    .stepOrder(stepDto.getStepOrder())
                    .description(stepDto.getDescription())
                    .imagePath(fileName != null ? "/uploads/" + fileName : null)
                    .board(board)
                    .build();

            recipeStepRepository.save(recipeStep);
        }
    }



    // 게시글 ID로 레시피 삭제
    public void deleteByBoardId(Long boardId) {
        recipeStepRepository.deleteByBoardId(boardId);
    }
}
