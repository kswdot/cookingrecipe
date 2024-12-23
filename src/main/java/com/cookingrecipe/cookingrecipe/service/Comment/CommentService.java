package com.cookingrecipe.cookingrecipe.service.Comment;

import com.cookingrecipe.cookingrecipe.dto.CommentRequestDto;
import com.cookingrecipe.cookingrecipe.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {

    // 댓글 생성
    CommentResponseDto create(CommentRequestDto requestDto);

    // 댓글 수정
    CommentResponseDto update(Long commentId, CommentRequestDto requestDto);

    // 댓글 조회
    List<CommentResponseDto> findByBoard(Long boardId);

    // 댓글 삭제
    void delete(Long commentId);
}
