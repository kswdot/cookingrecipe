package com.cookingrecipe.cookingrecipe.repository;

import com.cookingrecipe.cookingrecipe.domain.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;



    // 모든 게시글 검색 - 최신순
    @Override
    public List<Board> findAllByDateDesc() {
        QBoard board = QBoard.board;
        QImage image = QImage.image;

        return queryFactory
                .selectFrom(board)
                .leftJoin(board.images, image).fetchJoin()
                .orderBy(board.createdDate.desc())
                .fetch();
    }


    // 검색 조건에 따른 게시글 조회 - 최신순
    @Override
    public List<Board> searchBoards(String keyword, String ingredient, String nickname) {
        QBoard board = QBoard.board;

        BooleanExpression condition = chooseSingleCondition(keyword, ingredient, nickname);
        
        // 검색 조건이 없을 때 전체 게시글 반환
        if (condition == null) {
            return queryFactory
                    .selectFrom(board)
                    .orderBy(board.createdDate.desc())
                    .fetch();
        }

        // 검색 조건이 있을 때만 검색 실행
        return queryFactory
                .selectFrom(board)
                .where(condition)
                .orderBy(board.createdDate.desc())
                .fetch();
    }


    // 검색 조건에 따른 게시글 조회 - 좋아요 순
    @Override
    public List<Board> searchBoardsOrderByLikes(String keyword, String ingredient, String nickname) {
        QBoard board = QBoard.board;

        BooleanExpression condition = chooseSingleCondition(keyword, ingredient, nickname);

        if (condition == null) {
            return queryFactory
                    .selectFrom(board)
                    .orderBy(board.createdDate.desc())
                    .fetch();
        }

        return queryFactory
                .selectFrom(board)
                .where(condition)
                .orderBy(board.likeCount.desc())
                .fetch();
    }

    // 검색 조건이 있을 때
    private BooleanExpression chooseSingleCondition(String keyword, String ingredient, String nickname) {
        if (keyword != null) {
            return titleOrContentContains(keyword);
        } else if (ingredient != null) {
            return ingredientContains(ingredient);
        } else if (nickname != null) {
            return writerContains(nickname);
        }

        return null;
    }

    // 제목+내용 검색
    private BooleanExpression titleOrContentContains(String keyword) {
        QBoard board = QBoard.board;
        return board.title.contains(keyword).or(board.content.contains(keyword));
    }

    // 재료 검색
    private BooleanExpression ingredientContains(String ingredient) {
        QBoard board = QBoard.board;
        return board.ingredient.contains(ingredient);
    }

    // 작성자 검색
    private BooleanExpression writerContains(String nickname) {
        QBoard board = QBoard.board;
        return board.nickname.contains(nickname);
    }


    // 카테고리에 따른 레시피 조회 - 최신순
    @Override
    public List<Board> findByCategory(Category category) {
        QBoard board = QBoard.board;

        return queryFactory
                .selectFrom(board)
                .where(board.category.eq(category))
                .orderBy(board.createdDate.desc())
                .fetch();
    }


    // 카테고리에 따른 레시피 조회 - 좋아요 순
    @Override
    public List<Board> findByCategoryOrderByLikes(Category category) {
        QBoard board = QBoard.board;

        return queryFactory
                .selectFrom(board)
                .where(board.category.eq(category))
                .orderBy(board.likeCount.desc())
                .fetch();
    }


    // 요리 방법에 따른 레시피 조회 - 최신순
    @Override
    public List<Board> findByMethod(Method method) {
        QBoard board = QBoard.board;

        return queryFactory
                .selectFrom(board)
                .where(board.method.eq(method))
                .orderBy(board.createdDate.desc())
                .fetch();

    }


    // 요리 방법에 따른 레시피 조회 - 좋아요순
    @Override
    public List<Board> findByMethodOrderByLikes(Method method) {
        QBoard board = QBoard.board;

        return queryFactory
                .selectFrom(board)
                .where(board.method.eq(method))
                .orderBy(board.likeCount.desc())
                .fetch();
    }


    // 특정 사용자의 북마크에 저장된 레시피 조회
    @Override
    public List<Board> findBookmarkedRecipeByUser(Long userId) {
        QBoard board = QBoard.board;
        QBookmark bookmark = QBookmark.bookmark;

        return queryFactory
                .select(board)
                .from(bookmark)
                .join(bookmark.board, board)
                .where(bookmark.user.id.eq(userId))
                .orderBy(bookmark.createdDate.desc())
                .fetch();
    }


    // 사용자의 ID로 레시피 조회
    @Override
    public List<Board> findByUserId(Long userId) {
        QBoard board = QBoard.board;
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(board)
                .join(board.user, user)
                .where(user.id.eq(userId))
                .orderBy(board.createdDate.desc())
                .fetch();
    }


    // 인기 레시피 TOP 10 조회 (좋아요 순 기준)
    @Override
    public List<Board> findTopRecipesByLikes(int limit) {
        QBoard board = QBoard.board;

        return queryFactory
                .selectFrom(board)
                .orderBy(board.likeCount.desc())
                .limit(limit)
                .fetch();
    }


    // 이 달(한 달)의 레시피 TOP 10 조회
    @Override
    public List<Board> findMonthlyRecipesByLikes(int limit) {
        QBoard board = QBoard.board;

        // 현재 월의 첫째 날과 마지막 날 계산
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        return queryFactory
                .selectFrom(board)
                .where(board.createdDate.between(
                        startOfMonth.atStartOfDay(),
                        endOfMonth.atTime(23, 59, 59))
                )
                .limit(limit)
                .orderBy(board.likeCount.desc())
                .fetch();
    }
}
