package com.team.puddy.domain.article.repository.querydsl;

import com.team.puddy.domain.article.domain.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface ArticleQueryRepository {

    Optional<Article> findArticleWithUserById(Long articleId);


    Slice<Article> findAllByTitleStartingWithOrderByCreatedDateDesc(String keyword, Pageable pageable);

    List<Article> findPopularArticleList();

    List<Article> findRecentArticleList();

    Optional<Article> findArticleForModify(Long articleId, Long userId);

    Slice<Article> findAllByTitleStartingWithOrderByCreatedDateAsc(String keyword, Pageable pageable);

    Slice<Article> findAllByTitleStartingWithOrderByViewCountDesc(String keyword, Pageable pageable);
}