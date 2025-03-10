package com.cookingrecipe.cookingrecipe.domain.Board;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
public class RecipeStep {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int stepOrder; // 단계 번호

    @NotNull
    private String description;

    private String imagePath;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    @JsonBackReference
    private Board board;


    @Builder
    public RecipeStep(int stepOrder, String description, String imagePath, Board board) {
        if (stepOrder < 1) {
            throw new IllegalArgumentException("단계 번호는 1 이상이어야 합니다.");
        }
        this.stepOrder = stepOrder;
        this.description = description;
        this.imagePath = imagePath;
        this.board = board;
    }


    public void update(String description, String imagePath, @NotNull int stepOrder) {
        if (stepOrder < 1) {
            throw new IllegalArgumentException("단계 번호는 1 이상이어야 합니다.");
        }
        this.description = description;
        this.imagePath = imagePath;
        this.stepOrder = stepOrder;
    }

    // 기존 메서드 유지
    public void update(String description, String imagePath) {
        this.description = description;
        this.imagePath = imagePath;
    }

    public void update(String description, int stepOrder, String imagePath) {
        this.description = description;
        this.stepOrder = stepOrder;
        this.imagePath = imagePath;
    }




    // 연관 관계 편의 메서드
    public void setBoard(Board board) {
        this.board = board;
        if (!board.getRecipeSteps().contains(this)) {
            board.getRecipeSteps().add(this);
        }
    }
}
