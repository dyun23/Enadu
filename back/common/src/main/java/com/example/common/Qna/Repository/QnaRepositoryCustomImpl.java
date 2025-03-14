package com.example.common.Qna.Repository;

import com.example.common.Qna.model.Resolved;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import com.example.common.Qna.model.Entity.QnaBoard;
import com.example.common.Qna.model.Entity.QQnaBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QnaRepositoryCustomImpl implements QnaRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    QQnaBoard qQnaBoard = QQnaBoard.qnaBoard;

    @Override
    public Page<QnaBoard> findByKeyword(String keyword, boolean useSuperCategory, Long categoryId, String type, String resolved, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qQnaBoard.enable.isTrue());


        if (categoryId != null && categoryId != 0) {
            if (useSuperCategory) {
                builder.and(qQnaBoard.category.superCategory.id.eq(categoryId));
            }
            else{
                builder.and(qQnaBoard.category.id.eq(categoryId));
            }
        }

        if (keyword != null && !keyword.equals("")) {
            String trimmedKeyword = keyword.replaceAll("\\s+", "");

            String regex = "!\\[desc\\]\\(.*?\\)\\{\\{\\{.*?\\}\\}\\}";
            StringExpression trimmedTitle = Expressions.stringTemplate("REPLACE(REPLACE({0}, ' ', ''), {1}, '')", qQnaBoard.title, regex);
            StringExpression trimmedContent = Expressions.stringTemplate("REPLACE(REPLACE({0}, ' ', ''), {1}, '')", qQnaBoard.content, regex);

            if ("tc".equalsIgnoreCase(type)) {
                builder.and(trimmedTitle.contains(trimmedKeyword)
                        .or(trimmedContent.contains(trimmedKeyword)));
            } else if ("t".equalsIgnoreCase(type)) {
                builder.and(trimmedTitle.contains(trimmedKeyword));
            } else if ("c".equalsIgnoreCase(type)) {
                builder.and(trimmedContent.contains(trimmedKeyword));
            }
        }

        switch (resolved) {
            case "ALL" -> builder.and(qQnaBoard.resolved.eq(Resolved.UNSOLVED))
                    .or(qQnaBoard.resolved.eq(Resolved.RESOLVED));
            case "RESOLVED" -> builder.and(qQnaBoard.resolved.eq(Resolved.RESOLVED));
            case "UNSOLVED" -> builder.and(qQnaBoard.resolved.eq(Resolved.UNSOLVED));
        }

        JPAQuery<QnaBoard> query = queryFactory.selectFrom(qQnaBoard)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (pageable.getSort() != null) {
            pageable.getSort().forEach(order -> {

                switch (order.getProperty()) {
                    case "likeCount" -> query.orderBy(qQnaBoard.likeCount.desc());
                    case "createdAt" -> query.orderBy(qQnaBoard.createdAt.desc());
                }
            });
        } else {
            query.orderBy(qQnaBoard.createdAt.desc());
        }

        List<QnaBoard> results = query.fetch();

        // 페이징 처리 위해 보내는 값
        long total = queryFactory.selectFrom(qQnaBoard)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<QnaBoard> getQnaList(String resolved, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qQnaBoard.enable.isTrue());

        switch (resolved) {
            case "ALL" -> builder.and(qQnaBoard.resolved.eq(Resolved.UNSOLVED))
                    .or(qQnaBoard.resolved.eq(Resolved.RESOLVED));
            case "RESOLVED" -> builder.and(qQnaBoard.resolved.eq(Resolved.RESOLVED));
            case "UNSOLVED" -> builder.and(qQnaBoard.resolved.eq(Resolved.UNSOLVED));
        }

        JPAQuery<QnaBoard> query = queryFactory.selectFrom(qQnaBoard)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (pageable.getSort() != null) {
            pageable.getSort().forEach(order -> {

                switch (order.getProperty()) {
                    case "likeCount" -> query.orderBy(qQnaBoard.likeCount.desc());
                    case "createdAt" -> query.orderBy(qQnaBoard.createdAt.desc());
                }
            });
        } else {
            query.orderBy(qQnaBoard.createdAt.desc());
        }

        List<QnaBoard> results = query.fetch();


        // 페이징 처리 위해 보내는 값
        long total = queryFactory.selectFrom(qQnaBoard)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
