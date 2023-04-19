package com.team.puddy.domain.question.dto.response;

import com.team.puddy.domain.answer.dto.ResponseAnswerDto;
import com.team.puddy.domain.pet.domain.Pet;
import com.team.puddy.global.annotation.category.Category;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


public record QuestionResponseDto (Long questionId,
                                   String title,
                                   String content,
                                   String nickname,
                                   Pet pet,
                                   LocalDateTime createdDate,
                                   List<String> images,
                                   @Category String category,
                                   long viewCount,
                                   boolean isSolved,
                                   int postCategory,
                                   List<ResponseAnswerDto> answerList) {
    @Builder
    public QuestionResponseDto {}
}