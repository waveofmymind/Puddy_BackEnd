package com.team.puddy.domain.article.dto.request;

import lombok.Builder;

import java.util.List;

public record RequestArticleDto(String title,
                                String content,
                                List<String> tagList,
                                int postCategory) {

    @Builder
    public RequestArticleDto {
    }
}
