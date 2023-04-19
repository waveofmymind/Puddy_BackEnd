package com.team.puddy.domain.article.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.team.puddy.domain.article.domain.ArticleTag;
import com.team.puddy.domain.comment.dto.response.ResponseCommentDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public record ResponseArticleDto (Long articleId,
                                 String title,
                                 String content,
                                 String nickname,
                                 LocalDateTime createdDate,
                                 List<String> images,
                                 long viewCount,
                                 long likeCount,
                                 int postCategory,
                                 boolean isLike,
                                 @JsonIgnoreProperties({"article"})
                                 List<ArticleTag> tagList,
                                  @JsonIgnoreProperties({"article"})

                                  List<ResponseCommentDto> commentList) {

    @Builder
    public ResponseArticleDto {
    }
}
