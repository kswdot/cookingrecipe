package com.cookingrecipe.cookingrecipe.service;

import com.cookingrecipe.cookingrecipe.domain.Board;
import com.cookingrecipe.cookingrecipe.domain.Comment;
import com.cookingrecipe.cookingrecipe.domain.User;
import com.cookingrecipe.cookingrecipe.dto.CommentRequestDto;
import com.cookingrecipe.cookingrecipe.dto.CommentResponseDto;
import com.cookingrecipe.cookingrecipe.exception.BadRequestException;
import com.cookingrecipe.cookingrecipe.exception.UserNotFoundException;
import com.cookingrecipe.cookingrecipe.repository.BoardRepository;
import com.cookingrecipe.cookingrecipe.repository.CommentRepository;
import com.cookingrecipe.cookingrecipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    
    // 댓글 생성
    @Override
    public CommentResponseDto create(CommentRequestDto requestDto) {
        Board board = boardRepository.findById(requestDto.getBoardId())
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다"));
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));

        Comment comment = new Comment(user, board, requestDto.getContent());
        commentRepository.save(comment);

        return new CommentResponseDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getContent(),
                comment.getUser().getNickname(),
                comment.getCreatedDate()
        );
    }

    
    // 댓글 수정
    @Override
    public CommentResponseDto update(Long commentId, CommentRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        comment.updateContent(requestDto.getContent());

        return new CommentResponseDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getContent(),
                comment.getUser().getNickname(),
                comment.getCreatedDate()
        );
    }

    
    // 댓글 조회
    @Override
    public List<CommentResponseDto> findByBoard(Long boardId) {
        List<Comment> comments = commentRepository.findByBoardIdOrderByCreatedDateAsc(boardId);

        return comments.stream()
                .map(comment -> new CommentResponseDto(
                        comment.getId(),
                        comment.getUser().getId(),
                        comment.getContent(),
                        comment.getUser().getNickname(),
                        comment.getCreatedDate()
                ))
                .collect(Collectors.toList());
    }


    // 댓글 삭제
    @Override
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        commentRepository.delete(comment);
    }
}
