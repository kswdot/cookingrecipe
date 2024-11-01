package com.cookingrecipe.cookingrecipe.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@ToString(of = {"id"})
public class Image {

    @Id @GeneratedValue
    private Long id;

    @NotNull
    private String name;
    @NotNull
    private String path;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
}
