package com.team.puddy.domain.comment.dto.request;

import lombok.Builder;

public record RequestCommentDto(String content,
                                int postCategory) {

    @Builder
    public RequestCommentDto {
    }
}
