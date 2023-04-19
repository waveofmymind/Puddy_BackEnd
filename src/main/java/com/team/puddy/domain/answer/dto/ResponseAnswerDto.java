package com.team.puddy.domain.answer.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public record ResponseAnswerDto(Long id,
                                String content,
                                Long userId,
                                String nickname,
                                LocalDateTime createDate,
                                int postCategory,
                                String userRole,
                                boolean selected
                                ) {

    @Builder
    public ResponseAnswerDto {
    }
}
