package com.cookingrecipe.cookingrecipe.domain.Board;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Image {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;
    @NotNull
    private String path;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recipe_step_id") // RecipeStep과 매핑
    private RecipeStep recipeStep;


    public Image(String name, String path, RecipeStep recipeStep) {
        this.name = name;
        this.path = path;
        this.recipeStep = recipeStep;
    }
}
