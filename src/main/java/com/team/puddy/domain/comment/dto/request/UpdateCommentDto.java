package com.team.puddy.domain.comment.dto.request;

import lombok.Builder;

public record UpdateCommentDto(String content) {

    @Builder
    public UpdateCommentDto {
    }
}
