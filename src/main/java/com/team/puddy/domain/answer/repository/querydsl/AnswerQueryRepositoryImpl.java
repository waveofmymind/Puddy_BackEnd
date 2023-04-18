package com.team.puddy.domain.answer.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.puddy.domain.answer.domain.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.team.puddy.domain.answer.domain.QAnswer.answer;
import static com.team.puddy.domain.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class AnswerQueryRepositoryImpl implements AnswerQueryRepository{

    private final JPAQueryFactory queryFactory;

    public List<Answer> getAnswerList(Long questionId) {
        return queryFactory
                .selectFrom(answer)
                .leftJoin(answer.user,user).fetchJoin()
                .where(answer.question.id.eq(questionId))
                .orderBy(answer.modifiedDate.desc())
                .fetch();
    }

    public List<Answer> findAnswerListByUserId(Long userId) {

        return queryFactory.selectFrom(answer)
                .leftJoin(answer.user, user)
                .fetchJoin()
                .where(user.id.eq(userId))
                .fetch();
    }

    public Optional<Answer> findAnswerForUpdate(Long answerId, Long userId, Long questionId) {
        return Optional.ofNullable(queryFactory.selectFrom(answer)
                .where(answer.id.eq(answerId)
                        .and(user.id.eq(userId))
                        .and(answer.question.id.eq(questionId))) // 두 개의 조건을 연결
                .fetchOne());
    }

    public boolean existsAnswer(Long answerId, Long questionId, Long userId) {
        return queryFactory.selectFrom(answer)
                .where(answer.id.eq(answerId)
                        .and(answer.user.id.eq(userId))
                        .and(answer.question.id.eq(questionId)))
                .fetchOne() != null;
    }
}
