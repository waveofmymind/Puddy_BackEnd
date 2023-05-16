package com.team.puddy.domain.question.dto.request;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

public record QuestionServiceRegister(

        @NotBlank String title,
        @NotBlank String content,
        @NotBlank String category,
        int postCategory

) {

    @Builder
    public QuestionServiceRegister {
    }
}
