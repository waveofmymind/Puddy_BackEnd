package com.team.puddy.domain.answer.dto;

import lombok.Builder;

public record RequestAnswerDto(String content,
                               int postCategory) {

    @Builder
    public RequestAnswerDto {
    }
}
