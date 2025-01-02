package com.cookingrecipe.cookingrecipe.repository.Board;

import com.cookingrecipe.cookingrecipe.domain.*;
import com.cookingrecipe.cookingrecipe.domain.Board.*;
import com.cookingrecipe.cookingrecipe.domain.User.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {


    private final JPAQueryFactory queryFactory;
    private final StringRedisTemplate redisTemplate;
    private final BoardRepository boardRepository;



    // 모든 게시글 검색 - 최신순
    @Override
    public Page<Board> findAllByDateDesc(Pageable pageable) {
        QBoard board = QBoard.board;
        QRecipeStep recipeStep = QRecipeStep.recipeStep;

        List<Board> boards = queryFactory
                .selectFrom(board)
                .leftJoin(board.recipeSteps, recipeStep).fetchJoin()
                .where(recipeStep.stepOrder.eq(
                        queryFactory.select(recipeStep.stepOrder.max())
                                .from(recipeStep)
                                .where(recipeStep.board.eq(board))
                ))
                .orderBy(board.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 게시글 개수 가져오기
        Long totalCount = Optional.ofNullable(queryFactory
                        .select(board.count())
                        .from(board)
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(boards, pageable, totalCount);
    }


    // 검색 조건에 따른 게시글 조회 - 최신순
    @Override
    public Page<Board> searchBoards(String searchCriteria, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QRecipeStep recipeStep = QRecipeStep.recipeStep;

        BooleanExpression condition = createCondition(searchCriteria, keyword);

        // 게시글 리스트 조회 (페이징 적용)
        List<Board> boards = queryFactory
                .selectFrom(board)
                .leftJoin(board.recipeSteps, recipeStep)
                .where(condition)
                .orderBy(board.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 게시글 개수 조회 (검색 조건 적용)
        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .where(condition) // 검색 조건 적용
                .fetchOne();

        return new PageImpl<>(boards, pageable, totalCount != null ? totalCount : 0);
    }


    // 검색 조건에 따른 게시글 조회 - 좋아요 순
    @Override
    public Page<Board> searchBoardsOrderByLikes(String searchCriteria, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QRecipeStep recipeStep = QRecipeStep.recipeStep;

        BooleanExpression condition = createCondition(searchCriteria, keyword);

        // 게시글 리스트 조회
        List<Board> boards = queryFactory
                .selectFrom(board)
                .leftJoin(board.recipeSteps, recipeStep) // 연관 데이터 유지, fetchJoin 제거
                .where(condition)
                .orderBy(board.likeCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 게시글 개수 조회
        Long totalCount = queryFactory
                .select(board.count())
                .from(board)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(boards, pageable, totalCount != null ? totalCount : 0);
    }


    // 검색 조건 생성 메서드
    private BooleanExpression createCondition(String searchCriteria, String keyword) {
        QBoard board = QBoard.board;

        if (searchCriteria == null || keyword == null || keyword.isBlank()) {
            return null; // 검색 조건 없음
        }

        switch (searchCriteria) {
            case "title":
                return board.title.contains(keyword).or(board.content.contains(keyword));
            case "ingredient":
                return board.ingredient.contains(keyword);
            case "nickname":
                return board.nickname.contains(keyword);
            default:
                throw new IllegalArgumentException("Invalid searchCriteria: " + searchCriteria);
        }
    }


    // 카테고리에 따른 레시피 조회 - 최신순
    @Override
    public List<Board> findByCategory(Category category) {
        QBoard board = QBoard.board;
        QRecipeStep recipeStep = QRecipeStep.recipeStep;

        return queryFactory
                .selectFrom(board)
                .leftJoin(board.recipeSteps, recipeStep).fetchJoin()
                .where(board.category.eq(category)
                        .and(recipeStep.stepOrder.eq(
                                queryFactory.select(recipeStep.stepOrder.max())
                                        .from(recipeStep)
                                        .where(recipeStep.board.eq(board))
                        ))
                )
                .orderBy(board.createdDate.desc())
                .fetch();
    }


    // 카테고리에 따른 레시피 조회 - 좋아요 순
    @Override
    public List<Board> findByCategoryOrderByLikes(Category category) {
        QBoard board = QBoard.board;
        QRecipeStep recipeStep = QRecipeStep.recipeStep;

        return queryFactory
                .selectFrom(board)
                .leftJoin(board.recipeSteps, recipeStep).fetchJoin()
                .where(board.category.eq(category)
                        .and(recipeStep.stepOrder.eq(
                                queryFactory.select(recipeStep.stepOrder.max())
                                        .from(recipeStep)
                                        .where(recipeStep.board.eq(board))
                                ))
                )
                .orderBy(board.likeCount.desc())
                .fetch();
    }


    // 요리 방법에 따른 레시피 조회 - 최신순
    @Override
    public List<Board> findByMethod(Method method) {
        QBoard board = QBoard.board;
        QRecipeStep recipeStep = QRecipeStep.recipeStep;

        return queryFactory
                .selectFrom(board)
                .leftJoin(board.recipeSteps, recipeStep).fetchJoin()
                .where(board.method.eq(method)
                        .and(recipeStep.stepOrder.eq(
                                queryFactory.select(recipeStep.stepOrder.max())
                                        .from(recipeStep)
                                        .where(recipeStep.board.eq(board))
                        ))
                )
                .orderBy(board.createdDate.desc())
                .fetch();
    }


    // 요리 방법에 따른 레시피 조회 - 좋아요순
    @Override
    public List<Board> findByMethodOrderByLikes(Method method) {
        QBoard board = QBoard.board;
        QRecipeStep recipeStep = QRecipeStep.recipeStep;

        return queryFactory
                .selectFrom(board)
                .leftJoin(board.recipeSteps, recipeStep).fetchJoin()
                .where(board.method.eq(method)
                        .and(recipeStep.stepOrder.eq(
                                queryFactory.select(recipeStep.stepOrder.max())
                                        .from(recipeStep)
                                        .where(recipeStep.board.eq(board))
                        ))
                )
                .orderBy(board.likeCount.desc())
                .fetch();
    }


    // 특정 사용자의 북마크에 저장된 레시피 조회
    @Override
    public List<Board> findBookmarkedRecipeByUser(Long userId) {
        QBoard board = QBoard.board;
        QBookmark bookmark = QBookmark.bookmark;
        QRecipeStep recipeStep = QRecipeStep.recipeStep;

        return queryFactory
                .selectFrom(board)
                .join(bookmark).on(bookmark.board.eq(board))
                .leftJoin(board.recipeSteps, recipeStep).fetchJoin()
                .where(bookmark.user.id.eq(userId)
                        .and(recipeStep.stepOrder.eq(
                                queryFactory.select(recipeStep.stepOrder.max())
                                        .from(recipeStep)
                                        .where(recipeStep.board.eq(board))
                        ))
                )
                .orderBy(bookmark.createdDate.desc())
                .fetch();
    }


    // 사용자의 ID로 레시피 조회 - 마이페이지 사용
    @Override
    public List<Board> findByUserId(Long userId) {
        QBoard board = QBoard.board;
        QUser user = QUser.user;
        QRecipeStep recipeStep = QRecipeStep.recipeStep;

        return queryFactory
                .selectFrom(board)
                .leftJoin(board.recipeSteps, recipeStep).fetchJoin()
                .join(board.user, user)
                .where(user.id.eq(userId)
                        .and(recipeStep.stepOrder.eq(
                                queryFactory.select(recipeStep.stepOrder.max())
                                        .from(recipeStep)
                                        .where(recipeStep.board.eq(board))
                        ))
                )
                .orderBy(board.createdDate.desc())
                .fetch();
    }



    // 인기 레시피 TOP 10 조회 (좋아요 순 기준)
    @Override
    public List<Board> findTopRecipesByLikes(int limit) {
        QBoard board = QBoard.board;
        QRecipeStep recipeStep = QRecipeStep.recipeStep;

        return queryFactory
                .selectFrom(board)
                .leftJoin(board.recipeSteps, recipeStep).fetchJoin()
                .where(recipeStep.stepOrder.eq(
                        queryFactory.select(recipeStep.stepOrder.max())
                                .from(recipeStep)
                                .where(recipeStep.board.eq(board))
                ))
                .orderBy(board.likeCount.desc())
                .limit(limit)
                .fetch();
    }


    // 이 달(한 달)의 레시피 TOP 10 조회
    @Override
    public List<Board> findMonthlyRecipesByLikes(int limit) {
        QBoard board = QBoard.board;
        QRecipeStep recipeStep = QRecipeStep.recipeStep;

        // 현재 월의 첫째 날과 마지막 날 계산
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        return queryFactory
                .selectFrom(board)
                .leftJoin(board.recipeSteps, recipeStep).fetchJoin()
                .where(
                        board.createdDate.between(
                                startOfMonth.atStartOfDay(),
                                endOfMonth.atTime(23, 59, 59)
                        ).and(recipeStep.stepOrder.eq(
                                queryFactory.select(recipeStep.stepOrder.max())
                                        .from(recipeStep)
                                        .where(recipeStep.board.eq(board))
                        ))
                )
                .orderBy(board.likeCount.desc())
                .limit(limit)
                .fetch();
    }


    // 특정 게시글 조회 시 조회 수 증가 - Redis 사용
    @Override
    public boolean addViewCountWithRedis(Long boardId, Long userId) {
        String redisKey = "viewed:board:" + boardId + ":user:" + userId; // 고유 키 생성

        // Redis에 key 저장 (없으면 true 반환)
        boolean isNewView = Boolean.TRUE
                .equals(redisTemplate.opsForValue().setIfAbsent(redisKey, "1", Duration.ofHours(1)));

        if (isNewView) {
            // Redis에서 처음 본 경우에만 DB 업데이트
            System.out.println("New view detected for boardId: " + boardId);
            boardRepository.updateViewCount(boardId);
        } else {
            System.out.println("View already counted for boardId: " + boardId);
        }

        return isNewView;
    }
}

