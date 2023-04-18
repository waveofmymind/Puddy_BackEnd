package com.team.puddy.domain.answer.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.puddy.domain.answer.domain.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.team.puddy.domain.answer.domain.QAnswer.answer;
import static com.team.puddy.domain.user.domain.QUser.user;


public interface AnswerQueryRepository {

    public List<Answer> getAnswerList(Long questionId);
    public List<Answer> findAnswerListByUserId(Long userId);
    public Optional<Answer> findAnswerForUpdate(Long answerId,Long userId,Long questionId);
    public boolean existsAnswer(Long answerId, Long questionId, Long userId);
}
