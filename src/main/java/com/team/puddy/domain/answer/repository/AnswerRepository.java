package com.team.puddy.domain.answer.repository;

import com.team.puddy.domain.answer.domain.Answer;
import com.team.puddy.domain.answer.repository.querydsl.AnswerQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer,Long>, AnswerQueryRepository {
    @Modifying
    @Query("UPDATE Answer a SET a.selected = 1 where a.id = :answerId")
    void select(@Param("answerId") Long answerId);
}
