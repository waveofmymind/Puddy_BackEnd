package com.team.puddy.domain.review.dto;

import lombok.Builder;

public record RequestReviewDto(String content
) {
    @Builder
    public RequestReviewDto {
    }
}
