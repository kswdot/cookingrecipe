package com.cookingrecipe.cookingrecipe.service.RecipeStep;

import com.cookingrecipe.cookingrecipe.config.FileConfig;
import com.cookingrecipe.cookingrecipe.domain.Board.Board;
import com.cookingrecipe.cookingrecipe.domain.Board.RecipeStep;
import com.cookingrecipe.cookingrecipe.dto.RecipeStepDto;
import com.cookingrecipe.cookingrecipe.repository.RecipeStep.RecipeStepRepository;
import com.cookingrecipe.cookingrecipe.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeStepServiceImpl implements RecipeStepService {


    private final RecipeStepRepository recipeStepRepository;
    private final FileService fileService;


    @Override
    public List<RecipeStep> findByBoardId(Long boardId) {
        return recipeStepRepository.findByBoardId(boardId);
    }

    @Override
    public void saveRecipeSteps(Board board, List<RecipeStepDto> recipeStepDto) throws IOException {
        String uploadDir = FileConfig.createUploadDir(); // 통일된 경로 사용

        int stepOrder = 1;

        for (RecipeStepDto stepDto : recipeStepDto) {
            if (stepDto.getDescription() == null || stepDto.getDescription().isBlank()) {
                continue;
            }

            if (stepDto.getImage() == null || stepDto.getImage().isEmpty()) {
                String errorMessage = "레시피 단계 " + stepOrder + "에 이미지가 필요합니다";
                log.error("Validation error: {}", errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            String fileName = fileService.saveFile(stepDto.getImage(), uploadDir);


            // RecipeStep 생성 및 저장
            RecipeStep recipeStep = RecipeStep.builder()
                    .stepOrder(stepOrder++) // 단계 번호 자동 증가
                    .description(stepDto.getDescription())
                    .imagePath(fileName)
                    .board(board)
                    .build();

            recipeStepRepository.save(recipeStep);
        }
    }


    @Override
    public void updateRecipeSteps(Board board, List<RecipeStep> existingSteps, List<RecipeStepDto> stepDtos, String uploadDir) throws IOException {
        uploadDir = FileConfig.createUploadDir(); // 경로 통일

        Map<Integer, RecipeStep> existingStepMap = existingSteps.stream()
                .collect(Collectors.toMap(RecipeStep::getStepOrder, step -> step));

        for (RecipeStepDto stepDto : stepDtos) {
            RecipeStep step = existingStepMap.get(stepDto.getStepOrder());

            if (step != null) {
                // 기존 단계 업데이트
                updateExistingStep(step, stepDto, uploadDir);
            } else {
                // 새로운 단계 추가
                saveNewStep(board, stepDto, uploadDir);
            }

            // 업데이트된 단계는 맵에서 제거
            existingStepMap.remove(stepDto.getStepOrder());
        }

        // 제거된 단계 처리
        for (RecipeStep removedStep : existingStepMap.values()) {
            deleteExistingStep(removedStep, uploadDir);
        }
    }


    @Override
    public void saveNewStep(Board board, RecipeStepDto stepDto, String uploadDir) throws IOException {
            String fileName = stepDto.getImage() != null && !stepDto.getImage().isEmpty()
                    ? fileService.saveFile(stepDto.getImage(), uploadDir)
                    : null; // 이미지가 없으면 null 처리

            RecipeStep recipeStep = RecipeStep.builder()
                    .stepOrder(stepDto.getStepOrder())
                    .description(stepDto.getDescription())
                    .imagePath(fileName)
                    .board(board)
                    .build();

            recipeStepRepository.save(recipeStep);

    }


    @Override
    public void updateExistingStep(RecipeStep existingStep, RecipeStepDto stepDto, String uploadDir) throws IOException {
        String newImagePath;

        if (stepDto.getImage() != null && !stepDto.getImage().isEmpty()) {
            // 새 이미지를 저장하고 기존 파일 삭제
            newImagePath = fileService.updateFile(stepDto.getImage(), uploadDir, existingStep.getImagePath());
        } else {
            // 새 이미지가 없으면 기존 경로 유지
            newImagePath = existingStep.getImagePath();
        }

        existingStep.update(stepDto.getDescription(), newImagePath);
        log.warn("Updated step: {}, ImagePath: {}", existingStep.getStepOrder(), newImagePath);
    }


    @Override
    public void deleteExistingStep(RecipeStep step, String uploadDir) throws IOException {
        fileService.deleteFile(uploadDir, step.getImagePath());
        recipeStepRepository.delete(step);
        log.warn("Deleted RecipeStep with ID: {}", step.getId());
    }
}
