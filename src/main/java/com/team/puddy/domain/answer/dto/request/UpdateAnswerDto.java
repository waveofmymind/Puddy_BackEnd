package com.team.puddy.domain.answer.dto.request;

import lombok.Builder;

public record UpdateAnswerDto(String content
                             ) {

    @Builder
    public UpdateAnswerDto {
    }
}
