package com.team.puddy.domain.question.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.puddy.domain.question.domain.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.team.puddy.domain.answer.domain.QAnswer.answer;
import static com.team.puddy.domain.expert.domain.QExpert.expert;
import static com.team.puddy.domain.image.domain.QImage.image;
import static com.team.puddy.domain.pet.domain.QPet.pet;
import static com.team.puddy.domain.question.domain.QQuestion.question;
import static com.team.puddy.domain.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QuestionQueryRepositoryImpl implements QuestionQueryRepository {

    private final JPAQueryFactory queryFactory;


    public Optional<Question> getQuestion(Long questionId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(question)
                .leftJoin(question.user, user).fetchJoin() // User 엔티티를 함께 조회하기 위한 조인
                .leftJoin(user.pet, pet).fetchJoin() // Pet 엔티티를 함께 조회하기 위한 조인
                .leftJoin(user.expert, expert).fetchJoin() // Expert 엔티티를 함께 조회하기 위한 조인
                .leftJoin(question.answerList, answer).fetchJoin() // Answer 엔티티를 함께 조회하기 위한 조인
                .leftJoin(answer.user, user).fetchJoin()
                .where(question.id.eq(questionId)) // 주어진 questionId에 해당하는 엔티티를 조회하기 위한 조건
                .fetchOne());
    }

    public Slice<Question> getQuestionList(Pageable pageable) {

        List<Question> questionList = queryFactory.selectFrom(question)
                .join(question.user, user).fetchJoin()
                .orderBy(question.modifiedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = questionList.size() > pageable.getPageSize();
        if (hasNext) {
            questionList.remove(questionList.size() - 1);
        }


        return new SliceImpl<>(questionList, pageable, hasNext);

    }



    public List<Question> getPopularQuestionList() {

        return queryFactory.selectFrom(question).distinct()
                .leftJoin(question.user, user).fetchJoin()
                .orderBy(question.viewCount.desc())
                .limit(5)
                .fetch();
    }

    public List<Question> getRecentQuestionList() {
        return queryFactory.selectFrom(question).distinct()
                .leftJoin(question.user, user).fetchJoin()
                .orderBy(question.modifiedDate.desc())
                .limit(5)
                .fetch();
    }

    public Optional<Question> findByIdWithUser(Long questionId) {
        return Optional.ofNullable(queryFactory.selectFrom(question)
                .leftJoin(question.user, user).fetchJoin()
                .where(question.id.eq(questionId))
                .fetchOne());
    }

    public Slice<Question> findQuestionListByUserId(Long userId, Pageable pageable) {
        List<Question> questionList = queryFactory.selectFrom(question)
                .innerJoin(question.user, user)
                .fetchJoin()
                .where(user.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(question.createdDate.desc())
                .fetch();

        boolean hasNext = questionList.size() > pageable.getPageSize();
        if (hasNext) {
            questionList.remove(questionList.size() - 1);
        }


        return new SliceImpl<>(questionList, pageable, hasNext);
    }

    public Optional<Question> findQuestionForModify(Long questionId, Long userId) {
        return Optional.ofNullable(queryFactory.selectFrom(question)
                .leftJoin(question.imageList, image).fetchJoin()
                .where(question.id.eq(questionId)
                        .and(question.user.id.eq(userId)))
                .fetchOne());
    }

    @Override
    public Slice<Question> findByTitleStartWithOrderByCreatedDateDesc(Pageable pageable, String keyword) {
        List<Question> questionList = queryFactory.selectFrom(question).distinct()
                .leftJoin(question.user, user).fetchJoin()
                .leftJoin(user.expert, expert).fetchJoin()
                .leftJoin(user.pet, pet).fetchJoin()
                .where(question.title.startsWith(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(question.createdDate.desc())
                .fetch();

        boolean hasNext = questionList.size() > pageable.getPageSize();
        if (hasNext) {
            questionList.remove(questionList.size() - 1);
        }


        return new SliceImpl<>(questionList, pageable, hasNext);
    }

    @Override
    public Slice<Question> findByTitleStartWithOrderByViewCountDesc(Pageable pageable, String keyword) {
        List<Question> questionList = queryFactory.selectFrom(question).distinct()
                .leftJoin(question.user, user).fetchJoin()
                .leftJoin(user.expert, expert).fetchJoin()
                .leftJoin(user.pet, pet).fetchJoin()
                .where(question.title.startsWith(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(question.viewCount.desc())
                .fetch();

        boolean hasNext = questionList.size() > pageable.getPageSize();
        if (hasNext) {
            questionList.remove(questionList.size() - 1);
        }


        return new SliceImpl<>(questionList, pageable, hasNext);
    }

    @Override
    public Slice<Question> findByTitleStartWithOrderByCreatedDate(Pageable pageable, String keyword) {
        List<Question> questionList = queryFactory.selectFrom(question).distinct()
                .leftJoin(question.user, user).fetchJoin()
                .leftJoin(user.expert, expert).fetchJoin()
                .leftJoin(user.pet, pet).fetchJoin()
                .where(question.title.startsWith(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(question.createdDate.asc())
                .fetch();

        boolean hasNext = questionList.size() > pageable.getPageSize();
        if (hasNext) {
            questionList.remove(questionList.size() - 1);
        }


        return new SliceImpl<>(questionList, pageable, hasNext);
    }

}