package com.team.puddy.domain.question.repository.querydsl;

import com.team.puddy.domain.question.domain.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface QuestionQueryRepository {

    Optional<Question> getQuestion(Long questionId);

    Slice<Question> getQuestionList(Pageable pageable);

    List<Question> getPopularQuestionList();

    List<Question> getRecentQuestionList();

    Optional<Question> findByIdWithUser(Long questionId);

    Slice<Question> findQuestionListByUserId(Long userId, Pageable pageable);


    Optional<Question> findQuestionForModify(Long questionId, Long userId);


    Slice<Question> findByTitleStartWithOrderByCreatedDateDesc(Pageable page, String keyword);

    Slice<Question> findByTitleStartWithOrderByViewCountDesc(Pageable page, String keyword);

    Slice<Question> findByTitleStartWithOrderByCreatedDate(Pageable page, String keyword);

}